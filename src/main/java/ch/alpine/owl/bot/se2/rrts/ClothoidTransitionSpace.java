// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.Tensor;

/** start and end points are from SE(2) or SE(2) Covering */
public enum ClothoidTransitionSpace implements TransitionSpace {
  ANALYTIC(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder()), //
  LEGENDRE(ClothoidBuilders.SE2_LEGENDRE.clothoidBuilder()), //
  COVERING(ClothoidBuilders.SE2_COVERING.clothoidBuilder()), //
  ;

  private final ClothoidBuilder clothoidBuilder;

  private ClothoidTransitionSpace(ClothoidBuilder clothoidBuilder) {
    this.clothoidBuilder = clothoidBuilder;
  }

  @Override // from TransitionSpace
  public ClothoidTransition connect(Tensor start, Tensor end) {
    return ClothoidTransition.of(clothoidBuilder, start, end);
  }
}
