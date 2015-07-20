import unittest

from bson.objectid import ObjectId
from bson.errors import InvalidId

from locator import RepositoryLocator
from memory import MemoryRepositoryLocator
import events
import commands
import searches
import handlers


def fake_event(oid=ObjectId()):
    event = events.Event('Cool event', [])
    event.oid = oid
    return event


class CreateEventCommandHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_the_event_is_added_to_the_repository(self):
        handler = handlers.CreateEventCommandHandler(commands.CreateEventCommand)

        event_id = handler.execute(commands.CreateEventCommand('Cool event', [
            {'name': 'Lea', 'email': 'lea@email.com', 'share': 1},
            {'name': 'Eva', 'share': 1}
        ]))

        entity = RepositoryLocator.events().entities[event_id]
        self.assertEqual('Cool event', entity.name)
        self.assertEqual('Lea', entity.participants[0].name)
        self.assertEqual('lea@email.com', entity.participants[0].email)
        self.assertEqual(1, entity.participants[0].share)
        self.assertEqual('Eva', entity.participants[1].name)
        self.assertEqual('', entity.participants[1].email)
        self.assertEqual(1, entity.participants[1].share)


class EventDetailsSearchHandlerTestCase(unittest.TestCase):
    def setUp(self):
        RepositoryLocator.initialize(MemoryRepositoryLocator())

    def tearDown(self):
        RepositoryLocator.initialize(None)

    def test_can_return_an_event_and_its_properties(self):
        event = fake_event()
        event.add_participant(events.Participant('Kim', '1'))
        RepositoryLocator.events().entities[event.oid] = event

        result = handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)\
            .execute(searches.EventDetailsSearch(str(event.oid)))

        self.assertEqual(result.oid, event.oid)
        self.assertEqual(result.name, 'Cool event')
        self.assertEqual(result.participants[0].name, 'Kim')
        self.assertListEqual(result.purchases, [])

    def test_an_exception_is_thrown_if_the_given_id_is_not_a_valid_objectid(self):
        handler = handlers.SearchEventDetailsHandler(searches.EventDetailsSearch)

        self.assertRaises(InvalidId, handler.execute, searches.EventDetailsSearch("hello"))
