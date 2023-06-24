// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

class TrajectoryWrapTest {
  @Test
  void testSimple() {
    StateTime stateTime = new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(4));
    TrajectorySample trajectorySample = new TrajectorySample(stateTime, null);
    TrajectoryWrap trajectoryWrap = TrajectoryWrap.of(List.of(trajectorySample));
    assertTrue(trajectoryWrap.isRelevant(RealScalar.of(3)));
    assertFalse(trajectoryWrap.isDefined(RealScalar.of(4)));
    assertThrows(Exception.class, () -> trajectoryWrap.getControl(RealScalar.of(3)));
    assertThrows(Exception.class, () -> trajectoryWrap.getSample(RealScalar.of(3)));
    assertThrows(Exception.class, () -> trajectoryWrap.getSample(RealScalar.of(4)));
  }
}
