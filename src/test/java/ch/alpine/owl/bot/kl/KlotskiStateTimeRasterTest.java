// code by jph
package ch.alpine.owl.bot.kl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;

public class KlotskiStateTimeRasterTest {
  @Test
  public void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      Tensor board = klotskiProblem.startState();
      Tensor key = KlotskiStateTimeRaster.INSTANCE.convertToKey(new StateTime(board, RealScalar.ONE));
      assertEquals(board, key);
    }
  }
}
