// code by jph
package ch.alpine.owl.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;

class TrajectoryEntryTest {
  @Test
  void testSimple() {
    TrajectoryEntry trajectoryEntry = Serialization.copy(new TrajectoryEntry(null, RealScalar.ONE));
    assertFalse(trajectoryEntry.point().isPresent());
    assertEquals(trajectoryEntry.variable(), RealScalar.ONE);
  }
}
