package agoodfriendalwayspayshisdebts.web.actions.event

import agoodfriendalwayspayshisdebts.command.email.SendReminderCommand
import com.vter.command.CommandBus
import net.codestory.http.Cookies
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class SendReminderTest extends Specification {
  CommandBus commandBus = Mock(CommandBus)

  SendReminder action

  def setup() {
    action = new SendReminder(commandBus)
  }

  def "posts the command in the bus"() {
    given:
    def cookies = Mock(Cookies)
    cookies.value("i18next") >> "fr"

    and:
    def eventId = UUID.randomUUID()
    def command = new SendReminderCommand()
    commandBus.send(command) >> Mock(CompletableFuture)

    when:
    def payload = action.send(eventId.toString(), cookies, command)

    then:
    command.eventId == eventId
    command.locale == "fr"

    and:
    payload.code() == 201
    payload.rawContent() instanceof CompletableFuture
  }
}