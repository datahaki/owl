// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.tensor.ext.Serialization;

class TrajectoryObstacleConstraintTest {
  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(null);
    Serialization.copy(plannerConstraint);
  }
}
