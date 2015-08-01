import abc

from locator import RepositoryLocator
from command_handlers import Handler
import internal_events
import searches
from errors import EntityNotFoundError
from factories import EventFactory
import calculation


DB = None


class SearchEventDetailsHandler(Handler):
    def __init__(self):
        super(SearchEventDetailsHandler, self).__init__(searches.EventDetailsSearch)

    def execute(self, search):
        event_details_document = DB['eventdetails_view'].find_one({'uuid': search.event_id})
        if event_details_document is None:
            raise EntityNotFoundError()

        return EventFactory.create_event_from_document(event_details_document)


class EventHandler(Handler):
    __metaclass__ = abc.ABCMeta

    @abc.abstractmethod
    def execute_event(self, event):
        pass

    def execute(self, event):
        self.execute_event(event)
        return None


class OnEventCreated(EventHandler):
    def __init__(self):
        super(OnEventCreated, self).__init__(internal_events.EventCreatedEvent)

    def execute_event(self, event):
        created_event = RepositoryLocator.events().get(event.event_id)
        DB['eventdetails_view'].insert(created_event.to_bson())


class OnPurchaseAddedUpdateView(EventHandler):
    def __init__(self):
        super(OnPurchaseAddedUpdateView, self).__init__(internal_events.PurchaseAddedEvent)

    def execute_event(self, event):
        updated_event = RepositoryLocator.events().get(event.event_id)
        DB['eventdetails_view'].update({'uuid': updated_event.uuid}, updated_event.to_bson())


class OnPurchaseAddedUpdateResult(EventHandler):
    def __init__(self):
        super(OnPurchaseAddedUpdateResult, self).__init__(internal_events.PurchaseAddedEvent)

    def execute_event(self, event):
        updated_event = RepositoryLocator.events().get(event.event_id)
        calculation_result = calculation.DebtsCalculator(updated_event).calculate()
        DB['eventresult_view'].update({'event_id': event.event_id}, calculation_result.to_bson(), upsert=True)
