// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.Collections;
import java.util.Optional;

import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeCollector;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class CatchyTrajectoryRegionQueryTest extends TestCase {
  public void testSimple() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTime stateTime = new StateTime(Tensors.vector(1), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(Collections.singletonList(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
  }

  public void testMembers1d() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTimeCollector stc = (StateTimeCollector) trq;
    assertTrue(stc.getMembers().isEmpty());
    StateTime stateTime = new StateTime(Tensors.vector(1), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(Collections.singletonList(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
    assertFalse(stc.getMembers().isEmpty());
  }

  public void testMembers2d() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2, 3), Tensors.vector(3, 4, 8));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTimeCollector stc = (StateTimeCollector) trq;
    assertTrue(stc.getMembers().isEmpty());
    StateTime stateTime = new StateTime(Tensors.vector(1, 2), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(Collections.singletonList(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
    assertFalse(stc.getMembers().isEmpty());
  }
}
