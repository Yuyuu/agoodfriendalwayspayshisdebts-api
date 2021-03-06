package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.model.RepositoryLocator;
import agoodfriendalwayspayshisdebts.model.event.Event;
import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.vter.command.CommandHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateEventCommandHandler implements CommandHandler<CreateEventCommand, UUID> {

  @Override
  public UUID execute(CreateEventCommand command) {
    final Event event = Event.createAndPublishInternalEvent(
        command.name,
        command.currency,
        extractParticipants(command.participants)
    );
    RepositoryLocator.events().add(event);
    return event.getId();
  }

  private static List<Participant> extractParticipants(List<Map<String, Object>> jsonParticipants) {
    return jsonParticipants.stream().map(
        json -> {
          String name = (String) json.get("name");
          int share = (int) json.get("share");
          String email = (String) json.get("email");
          return new Participant(name, share, email);
        }
    ).collect(Collectors.toList());
  }
}
