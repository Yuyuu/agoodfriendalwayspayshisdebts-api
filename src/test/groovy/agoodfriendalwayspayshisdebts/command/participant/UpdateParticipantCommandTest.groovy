package agoodfriendalwayspayshisdebts.command.participant

import spock.lang.Shared
import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class UpdateParticipantCommandTest extends Specification {
  @Shared
  ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()
  @Shared
  Validator validator = validatorFactory.validator

  def "an invalid email is a violation"() {
    given:
    def command = new UpdateParticipantCommand(email: "meh")

    when:
    def violations = validator.validate(command)

    then:
    violations.first().message == "INVALID_EMAIL"
  }

  def "an empty email should not be a violation"() {
    given:
    def command = new UpdateParticipantCommand(email: "")

    when:
    def violations = validator.validate(command)

    then:
    violations.size() == 0
  }
}
