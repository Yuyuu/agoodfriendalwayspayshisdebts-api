import unittest

import events


class PurchaseTestCase(unittest.TestCase):
    def test_has_purchaser(self):
        purchaser = 'Kim'
        purchase = events.Purchase(purchaser, '', 1)
        self.assertEqual(purchaser, purchase.purchaser)
        
    def test_has_title(self):
        title = 'Shopping'
        purchase = events.Purchase('', 'Shopping', 1)
        self.assertEqual(title, purchase.title)
        
    def test_has_amount(self):
        amount = 10.04
        purchase = events.Purchase('', '', amount)
        self.assertEqual(amount, purchase.amount)
        
    def test_has_no_participants_by_default(self):
        purchase = events.Purchase('', '', 1)
        self.assertEqual(len(purchase.participants), 0)

    def test_has_no_description_by_default(self):
        purchase = events.Purchase('', '', 1)
        self.assertIsNone(purchase.description)

    def test_can_add_a_participant(self):
        purchase = events.Purchase('', '', 1)
        purchase.add_participant('Bob')
        self.assertListEqual(purchase.participants, ['Bob'])

    def test_serialization(self):
        purchase = events.Purchase('Kim', 'Shopping', 10.04)
        purchase.add_participant('Bob')
        expected_purchase = {
            'purchaser': 'Kim',
            'title': 'Shopping',
            'amount': 10.04,
            'participants': ['Bob'],
            'description': None
        }
        self.assertDictEqual(expected_purchase, purchase.serialize())


class EventTestCase(unittest.TestCase):
    def test_generates_an_oid_upon_creation(self):
        event = events.Event('', [])
        self.assertIsNotNone(event.oid)

    def test_has_name(self):
        name = 'Cool event'
        event = events.Event(name, [])
        self.assertEqual(event.name, name)

    def test_has_participants(self):
        participants = ['Kim', 'Lea', 'Bob']
        event = events.Event('', participants)
        self.assertListEqual(event.participants, participants)

    def test_has_no_purchases_at_start(self):
        event = events.Event('', [])
        self.assertEqual(len(event.purchases), 0)

    def test_can_add_a_purchase(self):
        purchase = events.Purchase('Kim', 'Shopping', 2)
        event = events.Event('', [])
        event.add_purchase(purchase)
        self.assertListEqual(event.purchases, [purchase])

    def test_serialization(self):
        event = events.Event('Cool event', ['Bob', 'Kim'])
        purchase = events.Purchase('Bob', 'Gas', 10)
        event.add_purchase(purchase)
        expected_event = {
            '_id': event.oid,
            'name': 'Cool event',
            'participants': ['Bob', 'Kim'],
            'purchases': [{'purchaser': 'Bob', 'title': 'Gas', 'amount': 10, 'participants': [], 'description': None}]
        }
        self.assertDictEqual(expected_event, event.serialize())