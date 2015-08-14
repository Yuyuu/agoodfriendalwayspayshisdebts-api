package agoodfriendalwayspayshisdebts.command.expense

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.participant.Participant
import org.junit.Rule
import spock.lang.Specification

class AddExpenseCommandHandlerTest extends Specification {

  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  def "can add the expense to an event"() {
    given:
    def kim = new Participant("kim", 1, null)
    def event = new Event("event", [kim])
    RepositoryLocator.events().save(event)

    and:
    def command = new AddExpenseCommand(
        eventId: event.id,
        label: "label",
        purchaserUuid: kim.id().toString(),
        amount: 1,
        participantsUuids: [kim.id().toString()],
        description: "description"
    )

    when:
    new AddExpenseCommandHandler().execute(command)

    then:
    def expense = RepositoryLocator.events().get(event.id).expenses()[0]
    expense.label() == "label"
    expense.purchaserId() == kim.id()
    expense.amount() == 1
    expense.participantsIds() == [kim.id()]
    expense.description() == "description"
  }

  def "shares the expense between all the participants of the event if none is specified"() {
    given:
    def kim = new Participant("kim", 1, null)
    def lea = new Participant("lea", 1, null)
    def event = new Event("event", [kim, lea])
    RepositoryLocator.events().save(event)

    and:
    def command = new AddExpenseCommand(eventId: event.id, label: "test", purchaserUuid: kim.id().toString(), amount: 1)

    when:
    new AddExpenseCommandHandler().execute(command)

    then:
    def expense = RepositoryLocator.events().get(event.id).expenses()[0]
    expense.participantsIds() == event.participants()*.id()
  }
}
