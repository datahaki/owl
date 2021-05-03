// code by jph, ynager
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.RelabelDecision;
import ch.alpine.owl.math.VectorScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** for CostFunction that map to values of type {@link RealScalar} or {@link Quantity}.
 * 
 * in particular, not for cost functions that operate on {@link VectorScalar}
 * 
 * Remark: the implementation is designed to handle all cases:
 * exact precision, numerical inaccuracies, and quantities.
 * in the long term this universality may not be desirable. */
public class SimpleRelabelDecision implements RelabelDecision, Serializable {
  public static final RelabelDecision DEFAULT = new SimpleRelabelDecision(Chop._06);

  /** @param slack minimum threshold of improvement by a candidate
   * @return */
  public static RelabelDecision with(Scalar slack) {
    return new SimpleRelabelDecision(Chop.below(Sign.requirePositive(slack).number().doubleValue()));
  }

  /***************************************************/
  private final Chop chop;

  public SimpleRelabelDecision(Chop chop) {
    this.chop = chop;
  }

  @Override // from RelabelDecision
  public boolean doRelabel(GlcNode newNode, GlcNode oldNode) {
    return doRelabel(newNode.merit(), oldNode.merit());
  }

  // function is used in tests
  /* package */ boolean doRelabel(Scalar newMerit, Scalar oldMerit) {
    Scalar delta = oldMerit.subtract(newMerit);
    return Sign.isPositive(delta) //
        && Scalars.nonZero(chop.apply(delta));
  }
}
