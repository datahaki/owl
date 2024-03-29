// code by gjoel, jph
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Chop;

class IntersectionEntryFinderTest {
  @Test
  void testSE2() {
    Tensor goalSE2 = Tensors.vector(3, 1, 0);
    Tensor goal2D = Extract2D.FUNCTION.apply(goalSE2);
    TrajectoryEntryFinder finder = IntersectionEntryFinder.SPHERE_SE2;
    Optional<Tensor> waypointSE2 = finder.on(TrajectoryEntryFinderTest.WAYPOINTS).apply(Vector2Norm.of(goal2D)).point();
    assertTrue(waypointSE2.isPresent());
    Chop._01.requireClose(goalSE2, waypointSE2.get());
  }

  @Test
  void test2D() {
    Tensor goalSE2 = Tensors.vector(3, 1, 0);
    Tensor goal2D = Extract2D.FUNCTION.apply(goalSE2);
    TrajectoryEntryFinder finder = IntersectionEntryFinder.SPHERE_RN;
    Tensor waypoints = Tensor.of(TrajectoryEntryFinderTest.WAYPOINTS.stream().map(Extract2D.FUNCTION));
    Optional<Tensor> waypoint2D = finder.on(waypoints).apply(Vector2Norm.of(goal2D)).point();
    assertTrue(waypoint2D.isPresent());
    Chop._01.requireClose(goal2D, waypoint2D.get());
  }

  @Test
  void testSweep1() {
    // TODO OWL TEST
    // TrajectoryEntryFinder finder = IntersectionEntryFinder.SPHERE_SE2;
    // Tensor points = Tensor.of(finder.sweep(TrajectoryEntryFinderTest.WAYPOINTS).map(TrajectoryEntry::point).map(Optional::get));
    // Tensor distances = Tensor.of(TrajectoryEntryFinderTest.WAYPOINTS.stream().map(Extract2D.FUNCTION).map(Vector2Norm::of));
    // assertEquals(TrajectoryEntryFinderTest.WAYPOINTS.get(ArgMin.of(distances)), points.get(0));
    // assertEquals(TrajectoryEntryFinderTest.WAYPOINTS.get(ArgMax.of(distances)), Last.of(points));
    // Set<Tensor> uniques = points.stream().collect(Collectors.toSet());
    // assertEquals(TrajectoryEntryFinderTest.WAYPOINTS.length(), uniques.size());
  }

  @Test
  void testSweep2() {
    // TODO OWL TEST
    // Tensor waypoints = Tensors.fromString("{{0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}}");
    // TrajectoryEntryFinder finder = IntersectionEntryFinder.SPHERE_RN;
    // Tensor swept = Tensor.of(finder.sweep(waypoints).map(TrajectoryEntry::point).map(Optional::get));
    // assertEquals(waypoints, swept);
  }
}
