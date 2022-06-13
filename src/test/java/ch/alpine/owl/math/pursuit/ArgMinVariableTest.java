// code by gjoel
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.crv.clt.ClothoidBuilder;
import ch.alpine.sophus.crv.clt.ClothoidBuilders;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

class ArgMinVariableTest {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  private static final int DEPTH = 5;

  @Test
  void testInterpolation() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}");
    TrajectoryEntryFinder entryFinder = Serialization.copy(InterpolationEntryFinder.INSTANCE);
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Vector2Norm.of(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point().get().map(N.DOUBLE).map(Chop._06));
  }

  @Test
  void testIntersection() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = Serialization.copy(IntersectionEntryFinder.SPHERE_SE2);
    // ---
    // TODO OWL TEST investigate
    // Scalar var = ArgMinVariable.using(entryFinder, t -> Vector2Norm.of(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
    // assertEquals(Tensors.vector(1, 0, 0), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  }

  @Test
  void testGeodesic() throws ClassNotFoundException, IOException {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = Serialization.copy(new GeodesicInterpolationEntryFinder(CLOTHOID_BUILDER));
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Vector2Norm.of(Extract2D.FUNCTION.apply(t)), 20).apply(tensor);
    assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  }
  // public void testPerformance() {
  // Tensor timings = Tensors.empty();
  // Tensor tensor = Tensor.of(IntStream.range(-100, 100).mapToObj(RealScalar::of).map(Tensors.vector(1, 2, 0)::multiply)).unmodifiable();
  // assertEquals(Dimensions.of(tensor), Arrays.asList(200, 3));
  // TrajectoryEntryFinder entryFinder = IntersectionEntryFinder.SPHERE_SE2;
  // for (int i = 0; i < 20; i++) {
  // Timing timing = Timing.started();
  // Scalar var = ArgMinVariable.using(entryFinder, t -> Vector2Norm.of(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
  // timings.append(RealScalar.of(timing.seconds()));
  // assertEquals(Dimensions.of(tensor), Arrays.asList(200, 3));
  // assertEquals(Array.zeros(3), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  // }
  // Scalar mean_duration = N.DOUBLE.of((Scalar) Mean.of(timings)); // in seconds
  // assertTrue(Scalars.lessEquals(mean_duration, RealScalar.of(2.3))); // datahaki -> 0.20723770650000004
  // }
}
