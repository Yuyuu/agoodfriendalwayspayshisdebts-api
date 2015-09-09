package agoodfriendalwayspayshisdebts.search.event.result.synchronization

import agoodfriendalwayspayshisdebts.infrastructure.persistence.memory.WithMemoryRepository
import agoodfriendalwayspayshisdebts.model.RepositoryLocator
import agoodfriendalwayspayshisdebts.model.event.Event
import agoodfriendalwayspayshisdebts.model.event.EventCreatedInternalEvent
import agoodfriendalwayspayshisdebts.model.participant.Participant
import com.vter.search.WithJongo
import org.junit.Rule
import spock.lang.Specification

class OnEventCreatedTest extends Specification {
  @Rule
  WithJongo jongo = new WithJongo()

  @Rule
  WithMemoryRepository repository = new WithMemoryRepository()

  Participant kim = new Participant("kim", 1, null)
  String strKimId = kim.id().toString()
  Event event = new Event("", [kim])

  OnEventCreated handler

  def setup() {
    handler = new OnEventCreated(jongo.jongo())
    RepositoryLocator.events().save(event)
  }

  def "initializes the calculation result of the event"() {
    when:
    handler.executeEvent(new EventCreatedInternalEvent(event.id))

    then:
    def resultDocument = jongo.collection("eventresult_view").findOne()
    def participantsResultsDocument = resultDocument["participantsResults"]
    resultDocument["_id"] == event.id

    and:
    def kimResultDocument = participantsResultsDocument[strKimId]
    kimResultDocument["participantName"] == "kim"
    kimResultDocument["participantShare"] == 1
    kimResultDocument["totalSpent"] == 0D
    kimResultDocument["totalDebt"] == 0D
    kimResultDocument["debtsDetail"][strKimId] == null
  }
}
