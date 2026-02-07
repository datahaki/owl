// code by jph
package ch.alpine.sophus.math.bij;

import ch.alpine.sophus.lie.se2.Se2ForwardAction;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** Se2InverseAction is a substitute for the operation:
 * Inverse[SE2 matrix] dot point
 * 
 * Se2InverseAction is the canonic action of SE2 on R^2.
 * 
 * @see Se2ForwardAction */
/* package */ class Se2InverseAction implements TensorUnaryOperator {
  private final Scalar px;
  private final Scalar py;
  private final Scalar ca;
  private final Scalar sa;

  public Se2InverseAction(Tensor xya) {
    px = xya.Get(0);
    py = xya.Get(1);
    Scalar angle = xya.Get(2).negate();
    ca = Cos.FUNCTION.apply(angle);
    sa = Sin.FUNCTION.apply(angle);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Scalar qx = tensor.Get(0).subtract(px);
    Scalar qy = tensor.Get(1).subtract(py);
    return Tensors.of( //
        qx.multiply(ca).subtract(qy.multiply(sa)), //
        qx.multiply(sa).add(qy.multiply(ca)) //
    );
  }
}
