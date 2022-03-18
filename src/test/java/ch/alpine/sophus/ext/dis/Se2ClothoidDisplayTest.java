// code by jph
package ch.alpine.sophus.ext.dis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.crv.d2.PolygonArea;
import ch.alpine.sophus.hs.r2.Se2Parametric;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Clips;

public class Se2ClothoidDisplayTest {
  @Test
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

  @Test
  public void testShapeArea() {
    Scalar a1 = PolygonArea.of(Arrowhead.of(0.4));
    Scalar a2 = PolygonArea.of(Se2ClothoidDisplay.ANALYTIC.shape());
    Tolerance.CHOP.requireClose(a1, a2);
  }
}
