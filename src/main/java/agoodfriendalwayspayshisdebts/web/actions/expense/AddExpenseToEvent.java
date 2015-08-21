package agoodfriendalwayspayshisdebts.web.actions.expense;

import agoodfriendalwayspayshisdebts.command.expense.AddExpenseCommand;
import com.google.common.base.Throwables;
import com.vter.command.CommandBus;
import com.vter.infrastructure.bus.ExecutionResult;
import net.codestory.http.annotations.Post;
import net.codestory.http.annotations.Resource;
import net.codestory.http.payload.Payload;

import javax.inject.Inject;
import java.util.UUID;

@Resource
public class AddExpenseToEvent {

  @Inject
  public AddExpenseToEvent(CommandBus commandBus) {
    this.commandBus = commandBus;
  }

  @Post("/events/:stringifiedUuid/expenses")
  public Payload add(String stringifiedUuid, AddExpenseCommand command) {
    command.eventId = UUID.fromString(stringifiedUuid);

    final ExecutionResult<Void> result = commandBus.sendAndWaitResponse(command);
    if (!result.isSuccess()) {
      Throwables.propagate(result.error());
    }

    return Payload.created();
  }

  private final CommandBus commandBus;
}