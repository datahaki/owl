// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class PolygonAreaTest extends TestCase {
  public void testAreaTriangle() {
    Tensor poly = Tensors.fromString("{{1, 1}, {2, 1}, {1, 2}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, RationalScalar.HALF);
    ExactScalarQ.require(area);
  }

  public void testAreaCube() {
    Tensor poly = Tensors.fromString("{{1, 1}, {2, 1}, {2, 2}, {1, 2}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, RealScalar.ONE);
    ExactScalarQ.require(area);
  }

  public void testAreaEmpty() {
    Scalar area = PolygonArea.of(Tensors.empty());
    assertEquals(area, RealScalar.ZERO);
  }

  public void testAreaLine() {
    Tensor poly = Tensors.fromString("{{1, 1}, {2, 1}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, RealScalar.ZERO);
    ExactScalarQ.require(area);
  }

  public void testAreaPoint() {
    Tensor poly = Tensors.fromString("{{1, 1}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, RealScalar.ZERO);
    ExactScalarQ.require(area);
  }

  public void testAreaTriangleUnit() {
    Tensor poly = Tensors.fromString("{{1[m], 1[m]}, {2[m], 1[m]}, {1[m], 2[m]}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, Scalars.fromString("1/2[m^2]"));
    ExactScalarQ.require(area);
  }

  public void testAreaCubeUnit() {
    Tensor poly = Tensors.fromString("{{1[cm], 1[cm]}, {2[cm], 1[cm]}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, Scalars.fromString("0[cm^2]"));
    ExactScalarQ.require(area);
  }

  public void testAreaCirclePoints() {
    Scalar area = PolygonArea.of(CirclePoints.of(100));
    Chop._02.requireClose(area, Pi.VALUE);
  }

  public void testAreaCirclePointsReverse() {
    Scalar area = PolygonArea.of(Reverse.of(CirclePoints.of(100)));
    Chop._02.requireClose(area, Pi.VALUE.negate());
  }

  public void testArea1Unit() {
    Tensor poly = Tensors.fromString("{{1[m], 1[m]}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, Scalars.fromString("0[m^2]"));
    ExactScalarQ.require(area);
  }

  public void testArea2Unit() {
    Tensor poly = Tensors.fromString("{{1[m], 1[m]}, {2[m], 1[m]}}");
    Scalar area = PolygonArea.of(poly);
    assertEquals(area, Scalars.fromString("0[m^2]"));
    ExactScalarQ.require(area);
  }

  public void testFailScalar() {
    AssertFail.of(() -> PolygonArea.of(RealScalar.ONE));
  }

  public void testFailMatrix() {
    AssertFail.of(() -> PolygonArea.of(HilbertMatrix.of(3)));
  }
}
