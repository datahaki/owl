// code by gjoel
package ch.alpine.owl.math.pursuit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Chop;

class NaiveEntryFinderTest {
  private static final int DEPTH = 5;

  @Test
  public void testNaive() {
    Tensor tensor = Tensors.fromString("{{-4, -2, 0}, {-3, -2, 0}, {-3, -1, 0}, {-2, 0, 0}, {1, 0, 0}, {2, 1, 0}, {3, 1, 0}}").unmodifiable();
    TrajectoryEntryFinder entryFinder = NaiveEntryFinder.INSTANCE;
    // ---
    Scalar var = ArgMinVariable.using(entryFinder, t -> Vector2Norm.of(Extract2D.FUNCTION.apply(t)), DEPTH).apply(tensor);
    assertEquals(Tensors.vector(1, 0, 0), entryFinder.on(tensor).apply(var).point().get().map(Chop._06));
  }
}
