// code by gjoel
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;

public class TrajectoryEntryFinderTest {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  /* package */ static final Tensor WAYPOINTS = Tensors.of( //
      Tensors.vector(0, 0, 0), //
      Tensors.vector(1, 0.5, Math.PI / 4), //
      Tensors.vector(2, 1, 0), //
      Tensors.vector(4, 1, 0));

  @Test
  public void testNaive() {
    TrajectoryEntryFinder finder = NaiveEntryFinder.INSTANCE;
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(0.3)).point();
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(0, 0, 0), waypoint.get());
  }

  @Test
  public void testInterpolation() {
    TrajectoryEntryFinder finder = InterpolationEntryFinder.INSTANCE;
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(2.5)).point();
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }

  @Test
  public void testGeodesic() {
    TrajectoryEntryFinder finder = new GeodesicInterpolationEntryFinder(CLOTHOID_BUILDER);
    // ---
    Optional<Tensor> waypoint = finder.on(WAYPOINTS).apply(RealScalar.of(2.5)).point();
    assertTrue(waypoint.isPresent());
    assertEquals(Tensors.vector(3, 1, 0), waypoint.get());
  }

  // ---
  private static int INIT = 0;

  private static ScalarUnaryOperator func(Tensor tensor) {
    ++INIT;
    return s -> s;
  }

  @Test
  public void testOnce() {
    long count = IntStream.range(0, 10).mapToObj(RealScalar::of).map(func(null)).count();
    assertEquals(count, 10);
    assertEquals(INIT, 1);
  }
}
