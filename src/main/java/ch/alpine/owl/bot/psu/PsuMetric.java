// code by jph
package ch.alpine.owl.bot.psu;

import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Vector2Norm;

/* package */ enum PsuMetric implements TensorMetric {
  INSTANCE;

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    // mix of units [rad] and [rad/sec]
    return Vector2Norm.of(PsuWrap.INSTANCE.difference(p, q));
  }
}
