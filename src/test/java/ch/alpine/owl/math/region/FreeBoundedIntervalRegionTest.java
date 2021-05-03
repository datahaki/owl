// code by jph
package ch.alpine.owl.math.region;

import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class FreeBoundedIntervalRegionTest extends TestCase {
  public void testSimple() {
    FreeBoundedIntervalRegion bir = new FreeBoundedIntervalRegion(0, RealScalar.of(10), RealScalar.of(20));
    assertEquals(bir.signedDistance(Tensors.vector(+5)), RealScalar.of(-5));
    assertEquals(bir.signedDistance(Tensors.vector(10)), RealScalar.of(+0));
    assertEquals(bir.signedDistance(Tensors.vector(15)), RealScalar.of(+5));
    assertEquals(bir.signedDistance(Tensors.vector(20)), RealScalar.of(+0));
    assertEquals(bir.signedDistance(Tensors.vector(25)), RealScalar.of(-5));
  }

  public void testTrajectoryMember() {
    FreeBoundedIntervalRegion bir = new FreeBoundedIntervalRegion(0, RealScalar.of(10), RealScalar.of(20));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeInvariant(bir);
    assertFalse(trq.isMember(new StateTime(Tensors.vector(15), RealScalar.ZERO)));
    assertTrue(trq.isMember(new StateTime(Tensors.vector(5), RealScalar.ZERO)));
  }

  public void testFail() {
    AssertFail.of(() -> new FreeBoundedIntervalRegion(0, RealScalar.of(10), RealScalar.of(10)));
  }
}
