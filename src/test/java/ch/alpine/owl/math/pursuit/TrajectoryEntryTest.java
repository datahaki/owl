// code by jph
package ch.alpine.owl.math.pursuit;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class TrajectoryEntryTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TrajectoryEntry trajectoryEntry = Serialization.copy(new TrajectoryEntry(null, RealScalar.ONE));
    assertFalse(trajectoryEntry.point().isPresent());
    assertEquals(trajectoryEntry.variable(), RealScalar.ONE);
  }
}
