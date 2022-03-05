// code by jph
package ch.alpine.owl.math;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.math.TensorMetric;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.FrobeniusNorm;

// TODO OWL API design/naming not good
public class SimpleTensorMetric implements TensorMetric, Serializable {
  private final TensorDifference tensorDifference;

  /** @param tensorDifference that returns a vector */
  public SimpleTensorMetric(TensorDifference tensorDifference) {
    this.tensorDifference = Objects.requireNonNull(tensorDifference);
  }

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return FrobeniusNorm.of(tensorDifference.difference(p, q));
  }
}
