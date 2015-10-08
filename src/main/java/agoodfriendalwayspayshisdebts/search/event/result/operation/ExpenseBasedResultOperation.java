package agoodfriendalwayspayshisdebts.search.event.result.operation;

import agoodfriendalwayspayshisdebts.model.expense.Expense;

public abstract class ExpenseBasedResultOperation extends ResultOperation {

  public ExpenseBasedResultOperation(Expense expense) {
    this.expense = expense;
  }

  protected final double amountPerShare() {
    final int expenseNumberOfShares = expense.participantsIds().stream()
        .map(id -> participantsResults.get(id).participantShare())
        .reduce(0, Integer::sum);
    return expense.amount() / expenseNumberOfShares;
  }

  protected final Expense expense;
}
