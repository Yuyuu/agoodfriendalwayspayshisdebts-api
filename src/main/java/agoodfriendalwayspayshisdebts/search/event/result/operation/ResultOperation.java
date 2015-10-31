package agoodfriendalwayspayshisdebts.search.event.result.operation;

import agoodfriendalwayspayshisdebts.search.event.result.model.CalculationResult;
import agoodfriendalwayspayshisdebts.search.event.result.model.ParticipantResult;
import com.google.common.collect.Lists;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ResultOperation implements Consumer<CalculationResult> {

  @Override
  public void accept(CalculationResult result) {
    this.participantsResults = result.participantsResults;
    applyOperation();
  }

  protected abstract void applyOperation();

  protected final void mitigateDebtBetween(UUID aId, UUID bId) {
    final double aDebtTowardsB = participantsResults.get(aId).rawDebtTowards(bId);
    final double bDebtTowardsA = participantsResults.get(bId).rawDebtTowards(aId);
    final double mitigatedDebt = Math.abs(bDebtTowardsA - aDebtTowardsB);

    if (bDebtTowardsA > aDebtTowardsB) {
      participantsResults.get(bId).updateDebtTowards(aId, mitigatedDebt);
      participantsResults.get(aId).updateDebtTowards(bId, NO_DEBT);
    } else if (bDebtTowardsA < aDebtTowardsB) {
      participantsResults.get(bId).updateDebtTowards(aId, NO_DEBT);
      participantsResults.get(aId).updateDebtTowards(bId, mitigatedDebt);
    } else {
      participantsResults.get(bId).updateDebtTowards(aId, NO_DEBT);
      participantsResults.get(aId).updateDebtTowards(bId, NO_DEBT);
    }
  }

  protected static Predicate<UUID> hasNotId(UUID... ids) {
    return entityId -> !Lists.newArrayList(ids).contains(entityId);
  }

  protected Map<UUID, ParticipantResult> participantsResults;
  private static final double NO_DEBT = 0D;
}