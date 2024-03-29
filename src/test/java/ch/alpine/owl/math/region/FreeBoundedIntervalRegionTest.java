// code by jph
package ch.alpine.owl.math.region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Clips;

class FreeBoundedIntervalRegionTest {
  @Test
  void testSimple() {
    FreeBoundedIntervalRegion bir = new FreeBoundedIntervalRegion(0, Clips.interval(10, 20));
    assertEquals(bir.signedDistance(Tensors.vector(+5)), RealScalar.of(-5));
    assertEquals(bir.signedDistance(Tensors.vector(10)), RealScalar.of(+0));
    assertEquals(bir.signedDistance(Tensors.vector(15)), RealScalar.of(+5));
    assertEquals(bir.signedDistance(Tensors.vector(20)), RealScalar.of(+0));
    assertEquals(bir.signedDistance(Tensors.vector(25)), RealScalar.of(-5));
  }

  @Test
  void testTrajectoryMember() {
    FreeBoundedIntervalRegion bir = new FreeBoundedIntervalRegion(0, Clips.interval(10, 20));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeInvariant(bir);
    assertFalse(trq.test(new StateTime(Tensors.vector(15), RealScalar.ZERO)));
    assertTrue(trq.test(new StateTime(Tensors.vector(5), RealScalar.ZERO)));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> new FreeBoundedIntervalRegion(-1, Clips.unit()));
    assertThrows(Exception.class, () -> new FreeBoundedIntervalRegion(0, null));
  }
}
