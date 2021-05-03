// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.IOException;

import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class TrajectoryObstacleConstraintTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(null);
    Serialization.copy(plannerConstraint);
  }
}
