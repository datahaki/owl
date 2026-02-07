// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.io.Serializable;

import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.crv.clt.LagrangeQuadraticD;
import ch.alpine.sophis.ts.ClothoidTransition;
import ch.alpine.sophis.ts.Transition;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Sign;

public class ClothoidCurvatureQuery implements TransitionRegionQuery, Serializable {
  private final Clip clip;

  /** @param clip with positive width */
  public ClothoidCurvatureQuery(Clip clip) {
    Sign.requirePositive(clip.width());
    this.clip = clip;
  }

  @Override // from TransitionRegionQuery
  public boolean isDisjoint(Transition transition) {
    ClothoidTransition clothoidTransition = (ClothoidTransition) transition;
    LagrangeQuadraticD lagrangeQuadraticD = clothoidTransition.clothoid().curvature();
    return clip.isInside(lagrangeQuadraticD.head()) //
        && clip.isInside(lagrangeQuadraticD.tail());
  }
}
