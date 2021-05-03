// code by jph
package ch.alpine.owl.math.pursuit;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidPursuitsTest extends TestCase {
  public void testCurve() {
    for (int depth = 0; depth < 5; ++depth) {
      Tensor tensor = ClothoidPursuits.curve(Tensors.fromString("{10, 1, 1}"), depth);
      assertEquals(tensor.length(), (1 << depth) + 1);
    }
  }

  public void testFromTrajectory() {
    PursuitInterface pursuitInterface;
    Tensor trajectory = Tensors.of( //
        Tensors.vector(0, 0, 0), //
        Tensors.vector(2, 2, Math.PI / 2), //
        Tensors.vector(4, 4, Math.PI / 2));
    // ---
    pursuitInterface = ClothoidPursuits.fromTrajectory(trajectory, NaiveEntryFinder.INSTANCE, RealScalar.ONE);
    Chop._03.requireClose(pursuitInterface.firstRatio().get(), RationalScalar.HALF);
  }
}
