package agoodfriendalwayspayshisdebts.command.event;

import agoodfriendalwayspayshisdebts.command.event.validation.ValidParticipants;
import com.vter.command.Command;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateEventCommand implements Command<UUID> {

  @NotBlank(message = "EVENT_NAME_REQUIRED")
  public String name;

  @NotEmpty(message = "EVENT_CURRENCY_REQUIRED")
  public String currency;

  @NotEmpty(message = "PARTICIPANTS_REQUIRED")
  @ValidParticipants
  public List<Map<String, Object>> participants;
}
