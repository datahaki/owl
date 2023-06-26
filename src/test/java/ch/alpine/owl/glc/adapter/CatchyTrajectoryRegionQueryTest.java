// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeCollector;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class CatchyTrajectoryRegionQueryTest {
  @Test
  void testSimple() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTime stateTime = new StateTime(Tensors.vector(1), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(List.of(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
  }

  @Test
  void testMembers1d() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2), Tensors.vector(3, 4));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTimeCollector stc = (StateTimeCollector) trq;
    assertTrue(stc.getMembers().isEmpty());
    StateTime stateTime = new StateTime(Tensors.vector(1), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(List.of(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
    assertFalse(stc.getMembers().isEmpty());
  }

  @Test
  void testMembers2d() {
    Region<Tensor> region = new EllipsoidRegion(Tensors.vector(1, 2, 3), Tensors.vector(3, 4, 8));
    TrajectoryRegionQuery trq = CatchyTrajectoryRegionQuery.timeDependent(region);
    StateTimeCollector stc = (StateTimeCollector) trq;
    assertTrue(stc.getMembers().isEmpty());
    StateTime stateTime = new StateTime(Tensors.vector(1, 2), RealScalar.ZERO);
    Optional<StateTime> optional = trq.firstMember(List.of(stateTime));
    assertTrue(optional.isPresent());
    assertEquals(optional.get(), stateTime);
    assertFalse(stc.getMembers().isEmpty());
  }
}
