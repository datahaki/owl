// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class SimpleRnPointcloudDistanceTest extends TestCase {
  public void testSimple() {
    TensorScalarFunction tensorScalarFunction = SimpleRnPointcloudDistance.of(CirclePoints.of(20), Norm._2);
    Scalar distance = tensorScalarFunction.apply(Tensors.vector(1, 1));
    Clips.interval(0.4, 0.5).requireInside(distance);
  }
}
