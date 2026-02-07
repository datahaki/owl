// code by jph
package ch.alpine.sophus.math.bij;

import java.io.Serializable;

import ch.alpine.sophus.lie.se2.Se2ForwardAction;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** Se2Bijection forward
 * SE2 matrix dot point
 * 
 * Se2Bijection inverse
 * (SE2 matrix)^-1 dot point
 * 
 * @see Se2InverseAction */
public class Se2Bijection implements Bijection, Serializable {
  private final Tensor xya;

  /** @param xya == {px, py, angle} as member of Lie group SE2 */
  public Se2Bijection(Tensor xya) {
    this.xya = xya;
  }

  @Override // from Bijection
  public TensorUnaryOperator forward() {
    return new Se2ForwardAction(xya);
  }

  @Override // from Bijection
  public TensorUnaryOperator inverse() {
    return new Se2InverseAction(xya);
  }

  // @Override // from RigidBijection
  public Tensor forward_se2() {
    return Se2Matrix.of(xya);
  }
}
