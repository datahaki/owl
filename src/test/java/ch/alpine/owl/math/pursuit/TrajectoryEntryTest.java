// code by jph
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;

class TrajectoryEntryTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    TrajectoryEntry trajectoryEntry = Serialization.copy(new TrajectoryEntry(null, RealScalar.ONE));
    assertFalse(trajectoryEntry.point().isPresent());
    assertEquals(trajectoryEntry.variable(), RealScalar.ONE);
  }
}
