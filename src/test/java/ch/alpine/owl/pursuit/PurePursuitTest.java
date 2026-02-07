// code by jph
package ch.alpine.owl.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class PurePursuitTest {
  @Test
  void testRatioForwardLeftPositiveXUnit() {
    Tensor tensor = Tensors.fromString("{{.2[m], 0[m]}, {1[m], 1[m]}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, Quantity.of(1.0, "m"));
    Clip clip = Clips.interval(Quantity.of(1.2, "m^-1"), Quantity.of(1.5, "m^-1"));
    clip.requireInside(purePursuit.firstRatio().get());
  }

  @Test
  void testMatch2() {
    Tensor curve = Tensors.fromString("{{-0.4}, {0.6}, {1.4}, {2.2}}");
    assertThrows(Exception.class, () -> PurePursuit.fromTrajectory(curve, RealScalar.ONE));
  }

  @Test
  void testDistanceFail() {
    Tensor curve = Tensors.fromString("{{-0.4}, {0.6}, {1.4}, {2.2}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(curve, RealScalar.of(3.3));
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertFalse(optional.isPresent());
  }

  @Test
  void testRatioFail() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.5));
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertFalse(optional.isPresent());
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  @Test
  void testEquals() {
    Tensor tensor = Tensors.fromString("{{1, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.ONE);
    Optional<Tensor> optional = purePursuit.lookAhead();
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), UnitVector.of(2, 0));
    assertTrue(purePursuit.firstRatio().isPresent());
    assertEquals(purePursuit.firstRatio().get(), RealScalar.ZERO);
  }

  @Test
  void testEmpty() {
    Tensor tensor = Tensors.empty();
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.5));
    assertFalse(purePursuit.lookAhead().isPresent());
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  @Test
  void testDirectionFail() {
    Tensor tensor = Tensors.fromString("{{1, 1}, {0.3, 0.2}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.ONE);
    assertFalse(purePursuit.lookAhead().isPresent());
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  @Test
  void testRatioForward() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(1, 0)));
    Optional<Scalar> optional = purePursuit.firstRatio();
    Scalar rate = optional.get();
    assertTrue(Scalars.isZero(rate));
  }

  @Test
  void testRatioForwardPositiveX() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 0}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Scalar rate = purePursuit.firstRatio().get();
    assertTrue(Scalars.isZero(rate));
  }

  @Test
  void testRatioForwardLeftPositiveX() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, 1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.0));
    Clip clip = Clips.interval(1.2, 1.5);
    clip.requireInside(purePursuit.firstRatio().get());
  }

  @Test
  void testRatioForwardRightPositiveX() {
    Tensor tensor = Tensors.fromString("{{0.2, 0}, {1, -1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(1.0));
    Clip clip = Clips.interval(-1.5, -1.2);
    clip.requireInside(purePursuit.firstRatio().get());
  }

  @Test
  void testRatioBackRight() {
    Tensor tensor = Tensors.fromString("{{0, 0}, {-1, -1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(-1, -1)));
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  @Test
  void testRatioBackLeft() {
    Tensor tensor = Tensors.fromString("{{0, 0}, {-1, 1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(-1, 1)));
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  @Test
  void testRatioXZero() {
    Tensor tensor = Tensors.fromString("{{0, 0.3}, {0, 1}}");
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, RealScalar.of(0.5));
    Tensor dir = purePursuit.lookAhead().get();
    Tensor normal = Vector2Norm.NORMALIZE.apply(dir);
    Chop._12.requireClose(normal, Vector2Norm.NORMALIZE.apply(Tensors.vector(0, 1)));
    assertFalse(purePursuit.firstRatio().isPresent());
  }

  // shows problem with non-positive x
  @Test
  void testRatioLarge() {
    Tensor tensor = Tensors.fromString("{{0, 0}, {-100, 1}}");
    Scalar distance = DoubleScalar.of(100.0);
    PurePursuit purePursuit = PurePursuit.fromTrajectory(tensor, distance);
    Optional<Scalar> optional = purePursuit.firstRatio();
    assertFalse(optional.isPresent());
  }
}
