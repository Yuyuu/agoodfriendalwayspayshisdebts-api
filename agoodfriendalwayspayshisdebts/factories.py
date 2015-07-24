import events


class EventFactory:
    @staticmethod
    def create_event_from_document(document):
        participants = map(EventFactory.__create_participants, document['participants'])
        event = events.Event(document['name'], participants, document['uuid'])
        event.purchases = map(EventFactory.__create_purchases, document['purchases'])
        return event

    @staticmethod
    def __create_participants(bson_participant):
        return events.Participant(bson_participant['name'], bson_participant['share'], bson_participant['email'])

    @staticmethod
    def __create_purchases(bson_purchase):
        purchase = events.Purchase(
            bson_purchase['purchaser'],
            bson_purchase['amount'],
            bson_purchase['participants'],
            bson_purchase['label']
        )
        purchase.description = bson_purchase['description']
        return purchase