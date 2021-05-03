// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class GokartEntityTest extends TestCase {
  public void testFootprint() {
    assertTrue(MatrixQ.of(GokartEntity.SHAPE));
  }
}
