// code by astoll
package ch.alpine.owl.bot.ap;

import ch.alpine.owl.glc.adapter.AbstractMinTimeGoalManager;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Abs;

/* package */ class ApMinTimeGoalManager extends AbstractMinTimeGoalManager {
  // ---
  private final ApComboRegion apComboRegion;
  private final Scalar maxVerticalSpeed;

  public ApMinTimeGoalManager(ApComboRegion apComboRegion, Scalar maxVerticalSpeed) {
    super(apComboRegion);
    this.apComboRegion = apComboRegion;
    this.maxVerticalSpeed = Abs.FUNCTION.apply(maxVerticalSpeed);
  }

  @Override // from HeuristicFunction
  public Scalar minCostToGoal(Tensor tensor) {
    // Euclidian distance to goal region
    return apComboRegion.d_z(tensor).divide(maxVerticalSpeed);
  }
}
