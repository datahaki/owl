// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.r2.Se2Parametric;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.sophus.ply.PolygonArea;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class Se2ClothoidDisplayTest extends TestCase {
  public void testSimple() {
    // 1 2.5180768787131558
    // 2 2.5597567801548426
    // 3 2.5640965868005288
    // 4 2.564420707620397
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor q = Tensors.vector(0, 2, 0);
    Scalar scalar = Se2ClothoidDisplay.ANALYTIC.parametricDistance().distance(p, q);
    Clips.interval(2.542, 2.55).requireInside(scalar);
    Scalar result = Se2Parametric.INSTANCE.distance(p, q);
    assertEquals(result, RealScalar.of(2));
  }

  public void testShapeArea() {
    Scalar a1 = PolygonArea.FUNCTION.apply(Arrowhead.of(0.4));
    Scalar a2 = PolygonArea.FUNCTION.apply(Se2ClothoidDisplay.ANALYTIC.shape());
    Tolerance.CHOP.requireClose(a1, a2);
  }
}
