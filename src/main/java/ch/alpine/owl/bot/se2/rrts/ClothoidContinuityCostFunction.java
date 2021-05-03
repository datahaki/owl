// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.AbsSquared;

/** abs squared of difference in curvature at common node
 * 
 * if the coordinate unit is [m], then the cost has unit [m^-2] */
public enum ClothoidContinuityCostFunction implements TransitionCostFunction {
  INSTANCE;

  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  @Override // from TransitionCostFunction
  public Scalar cost(RrtsNode rrtsNode, Transition transition) {
    if (rrtsNode.isRoot())
      // TODO GJOEL why not just return "rrtsNode.costFromRoot()"?
      return rrtsNode.costFromRoot().zero();
    return transitionCost(rrtsNode.parent().state(), rrtsNode.state(), transition.end());
  }

  static Scalar transitionCost(Tensor p, Tensor q, Tensor r) {
    return AbsSquared.between( //
        CLOTHOID_BUILDER.curve(p, q).curvature().tail(), //
        CLOTHOID_BUILDER.curve(q, r).curvature().head());
  }
  // @Override // from TransitionCostFunction
  // public int influence() {
  // return 1;
  // }
}
