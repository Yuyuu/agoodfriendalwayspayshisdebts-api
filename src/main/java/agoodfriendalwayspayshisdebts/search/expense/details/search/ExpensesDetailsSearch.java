package agoodfriendalwayspayshisdebts.search.expense.details.search;

import agoodfriendalwayspayshisdebts.search.expense.details.model.ExpensesDetails;
import com.vter.search.Search;

import java.util.UUID;

public class ExpensesDetailsSearch extends Search<ExpensesDetails> {

  public ExpensesDetailsSearch(UUID eventId) {
    this.eventId = eventId;
  }

  public final UUID eventId;
}
