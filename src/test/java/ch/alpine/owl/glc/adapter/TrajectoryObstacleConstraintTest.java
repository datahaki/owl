// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.tensor.ext.Serialization;

public class TrajectoryObstacleConstraintTest {
  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(null);
    Serialization.copy(plannerConstraint);
  }
}
