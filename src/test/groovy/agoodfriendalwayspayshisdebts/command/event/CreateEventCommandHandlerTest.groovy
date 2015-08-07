package agoodfriendalwayspayshisdebts.command.event

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.participant.Participant
import org.junit.Rule
import spock.lang.Specification

class CreateEventCommandHandlerTest extends Specification {

  @Rule
  WithMemoryRepository memoryRepository = new WithMemoryRepository()

  def "can add the event to the repository"() {
    given:
    def command = new CreateEventCommand(name: "cool event", participants: [new Participant("kim", 1, null)])

    when:
    UUID id = new CreateEventCommandHandler().execute(command)

    then:
    def event = RepositoryLocator.events().get(id)
    event.name() == "cool event"
    event.participants().first().name() == "kim"
  }
}