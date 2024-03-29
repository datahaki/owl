// code by jph
package ch.alpine.owl.bot.kl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class KlotskiModelTest {
  @Test
  void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      assertEquals(klotskiProblem.startState(), //
          KlotskiModel.INSTANCE.f(klotskiProblem.startState(), Tensors.vector(0, 0, 0)));
    }
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      assertEquals(klotskiProblem.startState(), //
          KlotskiModel.INSTANCE.f(klotskiProblem.startState(), Tensors.vector(0, 0, 0)));
    }
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      assertEquals(klotskiProblem.startState(), //
          KlotskiModel.INSTANCE.f(klotskiProblem.startState(), Tensors.vector(0, 0, 0)));
    }
  }

  @Test
  void testMove() {
    KlotskiProblem klotskiProblem = Huarong.SNOWDROP.create();
    Tensor board = klotskiProblem.startState();
    Tensor next = KlotskiModel.INSTANCE.f(board, Tensors.vector(7, 1, 0));
    Tensor s = Tensors.fromString( //
        "{{0, 0, 1}, {1, 0, 0}, {1, 0, 3}, {1, 2, 0}, {2, 2, 1}, {3, 2, 3}, {3, 3, 1}, {3, 4, 2}, {3, 3, 3}, {3, 4, 0}, {3, 4, 3}}");
    assertEquals(next, s);
    assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).test(next));
  }
}
