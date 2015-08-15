package agoodfriendalwayspayshisdebts.search.event.details.model;

import agoodfriendalwayspayshisdebts.model.expense.Expense;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

public class ExpenseDetails {
  public String label;
  public UUID purchaserId;
  public double amount;
  public Set<UUID> participantsIds = Sets.newHashSet();
  public String description;

  public ExpenseDetails() {}

  public static ExpenseDetails fromExpense(Expense expense) {
    final ExpenseDetails expenseDetails = new ExpenseDetails();
    expenseDetails.label = expense.label();
    expenseDetails.purchaserId = expense.purchaserId();
    expenseDetails.amount = expense.amount();
    expenseDetails.participantsIds.addAll(expense.participantsIds());
    expenseDetails.description = expense.description();
    return expenseDetails;
  }
}
