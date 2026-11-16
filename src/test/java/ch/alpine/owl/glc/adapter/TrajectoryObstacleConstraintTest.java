// code by jph
package ch.alpine.owl.glc.adapter;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.tensor.ext.Serialization;

class TrajectoryObstacleConstraintTest {
  @Test
  void testSerializable() {
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(null);
    Serialization.copy(plannerConstraint);
  }
}
