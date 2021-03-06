package agoodfriendalwayspayshisdebts.model.event

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.activity.OperationPerformedInternalEvent
import agoodfriendalwayspayshisdebts.model.activity.OperationType
import agoodfriendalwayspayshisdebts.model.expense.Expense
import agoodfriendalwayspayshisdebts.model.expense.ExpenseAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.expense.ExpenseDeletedInternalEvent
import agoodfriendalwayspayshisdebts.model.expense.State
import agoodfriendalwayspayshisdebts.model.expense.UnknownExpense
import agoodfriendalwayspayshisdebts.model.participant.Participant
import agoodfriendalwayspayshisdebts.model.participant.ParticipantAddedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.UnknownParticipant
import com.vter.model.internal_event.WithEventBus
import org.junit.Rule
import spock.lang.Specification

class EventTest extends Specification {
  @Rule
  WithEventBus eventBus = new WithEventBus()

  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  def "can create an event with a name, a currency symbol and a list of participants"() {
    given:
    def event = new Event("cool event", "€", [new Participant("kim", 1, null)])

    expect:
    event.id != null
    event.name() == "cool event"
    event.currency() == "€"
    event.participants().first().name() == "kim"
    event.participants().first().eventId() == event.id
  }

  def "emits an internal event to notify of the event creation operation"() {
    when:
    def event = Event.createAndPublishInternalEvent("cool event", "€", [])

    then:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent.eventId == event.id
    internalEvent.creationDate != null
    internalEvent.operationType == OperationType.EVENT_CREATION
    internalEvent.operationData == "cool event"
  }

  def "emits an internal event when creating a new event"() {
    when:
    def event = Event.createAndPublishInternalEvent("cool event", "€", [new Participant("kim", 1, null)])

    then:
    def internalEvent = eventBus.bus.lastEvent(EventCreatedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
  }

  def "contains expenses"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("cool event", "€", [kim])

    when:
    def expense = new Expense("label", kim.id, 5, [kim.id], event.id)
    event.addExpense(expense)

    then:
    event.expenses().size() == 1
    event.expenses()[0] == expense
    expense.state() == State.ADDED
  }

  def "emits an internal event to notify of the expense added operation"() {
    given:
    def event = new Event("", "€", [])

    when:
    event.addExpense(new Expense("label", null, 0L, [], event.id))

    then:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent.eventId == event.id
    internalEvent.creationDate != null
    internalEvent.operationType == OperationType.NEW_EXPENSE
    internalEvent.operationData == "label"
  }

  def "emits an internal event to notify of the expense deleted operation"() {
    given:
    def event = new Event("", "€", [])
    def expense = new Expense("label", null, 0L, [], event.id)
    event.expenses().add(expense)

    when:
    event.deleteExpense(expense.id)

    then:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent.eventId == event.id
    internalEvent.creationDate != null
    internalEvent.operationType == OperationType.EXPENSE_DELETED
    internalEvent.operationData == "label"
  }

  def "emits an event when an expense is added"() {
    given:
    def event = new Event("event", "€", [new Participant("kim", 1, null)])

    when:
    def expense = new Expense("label", null, 2, [], event.id)
    event.addExpense(expense)

    then:
    def internalEvent = eventBus.bus.lastEvent(ExpenseAddedInternalEvent)
    internalEvent != null
    internalEvent.expense == expense
  }

  def "throws an error when attempting to delete an expense that does not exist"() {
    given:
    def event = new Event("", "€", [])

    when:
    event.deleteExpense(UUID.randomUUID())

    then:
    thrown(UnknownExpense)
  }

  def "can pass an expense to the deleted state"() {
    given:
    def event = new Event("", "€", [])
    def expense = new Expense("", null, 1, [], event.id)
    event.expenses().add(expense)

    when:
    def deletedExpense = event.deleteExpense(expense.id)

    then:
    deletedExpense == expense
    event.expenses().first() == expense
    deletedExpense.state() == State.DELETED
  }

  def "emits an event when an expense is deleted"() {
    given:
    def event = new Event("", "€", [])
    def expense = new Expense("label", null, 2, [], event.id)
    event.expenses().add(expense)

    when:
    event.deleteExpense(expense.id)

    then:
    def internalEvent = eventBus.bus.lastEvent(ExpenseDeletedInternalEvent)
    internalEvent != null
    internalEvent.expense == expense
  }

  def "contains participants"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("", "€", [kim])

    when:
    def ben = new Participant("ben", 1, null)
    event.addParticipant(ben)

    then:
    ben.eventId() == event.id
    event.participants().size() == 2
    event.participants().find { it.name() == "ben" } != null
  }

  def "emits an event to notify of the participant added operation"() {
    given:
    def event = new Event("", "€", [])

    when:
    event.addParticipant(new Participant("lea", 1, null))

    then:
    def internalEvent = eventBus.bus.lastEvent(OperationPerformedInternalEvent)
    internalEvent.eventId == event.id
    internalEvent.creationDate != null
    internalEvent.operationType == OperationType.NEW_PARTICIPANT
    internalEvent.operationData == "lea"
  }

  def "emits an event when a participant is added"() {
    given:
    def event = new Event("", "€", [new Participant("kim", 1, null)])

    when:
    def ben = new Participant("ben", 1, null)
    event.addParticipant(ben)

    then:
    def internalEvent = eventBus.bus.lastEvent(ParticipantAddedInternalEvent)
    internalEvent != null
    internalEvent.eventId == event.id
    internalEvent.participant == ben
  }

  def "can find a participant"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("", "€", [kim])

    expect:
    event.findParticipant(kim.id) == kim
  }

  def "throws an error if the searched participant does not exist"() {
    given:
    def event = new Event("", "€", [])

    when:
    event.findParticipant(UUID.randomUUID())

    then:
    thrown(UnknownParticipant)
  }
}
