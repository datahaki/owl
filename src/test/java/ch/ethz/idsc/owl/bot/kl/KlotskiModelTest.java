// code by jph
package ch.ethz.idsc.owl.bot.kl;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class KlotskiModelTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      assertEquals(klotskiProblem.getState(), //
          KlotskiModel.INSTANCE.f(klotskiProblem.getState(), Tensors.vector(0, 0, 0)));
    }
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      assertEquals(klotskiProblem.getState(), //
          KlotskiModel.INSTANCE.f(klotskiProblem.getState(), Tensors.vector(0, 0, 0)));
    }
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      assertEquals(klotskiProblem.getState(), //
          KlotskiModel.INSTANCE.f(klotskiProblem.getState(), Tensors.vector(0, 0, 0)));
    }
  }

  public void testMove() {
    Tensor board = Huarong.SNOWDROP.create().getState();
    Tensor next = KlotskiModel.INSTANCE.f(board, Tensors.vector(7, 1, 0));
    Tensor s = Tensors.fromString( //
        "{{0, 1, 2}, {1, 1, 1}, {1, 1, 4}, {1, 3, 1}, {2, 3, 2}, {3, 3, 4}, {3, 4, 2}, {3, 5, 3}, {3, 4, 4}, {3, 5, 1}, {3, 5, 4}}");
    assertEquals(next.get(0), s);
    assertFalse(KlotskiObstacleRegion.huarong().isMember(next));
  }
}
