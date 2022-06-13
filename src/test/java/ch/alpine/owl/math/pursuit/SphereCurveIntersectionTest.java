// code by jph, gjoel
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.ascona.util.api.Box2D;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.RotateLeft;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.qty.Quantity;

class SphereCurveIntersectionTest {
  @Test
  void testString() {
    Tensor curve = Box2D.SQUARE;
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.string(RotateLeft.of(curve, index));
      assertEquals(optional.isPresent(), index != 1);
      if (index != 1) {
        Tensor tensor = optional.get();
        ExactTensorQ.require(tensor);
        assertEquals(tensor, Tensors.vector(0.5, 0));
      }
    }
  }

  @Test
  void testCyclic() {
    Tensor curve = Box2D.SQUARE;
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.vector(0.5, 0));
    }
  }

  @Test
  void testQuantity() {
    Tensor curve = Tensors.fromString("{{0[m], 0[m]}, {1[m], 0[m]}, {1[m], 1[m]}, {0[m], 1[m]}}").unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(Quantity.of(RationalScalar.HALF, "m"));
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.fromString("{1/2[m], 0[m]}"));
    }
  }

  @Test
  void testPoint() {
    Tensor curve = Tensors.fromString("{{1, 0}}").unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    assertFalse(curveIntersection.cyclic(curve).isPresent());
    assertFalse(curveIntersection.string(curve).isPresent());
  }

  @Test
  void testEmpty() {
    Tensor curve = Tensors.empty().unmodifiable();
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    assertFalse(curveIntersection.cyclic(curve).isPresent());
    assertFalse(curveIntersection.string(curve).isPresent());
  }

  @Test
  void testOne() {
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    Tensor curve = Tensors.fromString("{{-1}, {0}, {1}, {2}, {3}}").unmodifiable();
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.of(RationalScalar.HALF));
    }
  }

  @Test
  void testThree() {
    CurveIntersection curveIntersection = new SphereCurveIntersection(RationalScalar.HALF);
    Tensor curve = Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}}").unmodifiable();
    for (int index = 0; index < curve.length(); ++index) {
      Optional<Tensor> optional = curveIntersection.cyclic(RotateLeft.of(curve, index));
      assertTrue(optional.isPresent());
      Tensor tensor = optional.get();
      ExactTensorQ.require(tensor);
      assertEquals(tensor, Tensors.vector(0.5, 0, 0));
    }
  }

  @Test
  void testAssisted() {
    Tensor base = UnitVector.of(3, 0);
    Tensor curve = TensorProduct.of(Range.of(0, 500), base);
    Timing timing1 = Timing.started();
    {
      for (int index = 0; index < curve.length() - 1; index += 5) {
        Scalar radius = RealScalar.of(index + 1.5);
        AssistedCurveIntersection curveIntersection = new SphereCurveIntersection(radius);
        Optional<Tensor> optional = curveIntersection.string(curve);
        assertEquals(base.multiply(radius), optional.get());
      }
      timing1.stop();
    }
    Timing timing2 = Timing.started();
    { // faster
      int prevIdx = 0;
      for (int index = 0; index < curve.length() - 1; index += 5) {
        Scalar radius = RealScalar.of(index + 1.5);
        AssistedCurveIntersection curveIntersection = new SphereCurveIntersection(radius);
        Optional<CurvePoint> optional = curveIntersection.string(curve, prevIdx);
        prevIdx = optional.get().getIndex();
        assertEquals(radius.number().intValue(), prevIdx);
        assertEquals(base.multiply(radius), optional.get().getTensor());
      }
      timing2.stop();
    }
    assertTrue(timing2.seconds() < timing1.seconds());
  }
}
