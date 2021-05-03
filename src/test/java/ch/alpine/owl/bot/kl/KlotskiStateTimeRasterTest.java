// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import junit.framework.TestCase;

public class KlotskiStateTimeRasterTest extends TestCase {
  public void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      Tensor board = klotskiProblem.startState();
      Tensor key = KlotskiStateTimeRaster.INSTANCE.convertToKey(new StateTime(board, RealScalar.ONE));
      assertEquals(board, key);
    }
  }
}
