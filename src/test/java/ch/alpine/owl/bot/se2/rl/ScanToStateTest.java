// code by jph
package ch.alpine.owl.bot.se2.rl;

import java.util.Random;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.ExponentialDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class ScanToStateTest extends TestCase {
  public void testSimple() {
    Tensor res = ScanToState.of(Tensors.vector(1, 2, 3));
    assertEquals(res.length(), 2);
  }

  public void testCollision() {
    Tensor res = ScanToState.of(Tensors.vector(0, 0, 0));
    assertEquals(res.length(), 2);
    assertTrue(res.get(0).length() < 2);
  }

  public void testUnique() {
    Random random = new Random();
    Distribution distribution = ExponentialDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Tensor range = RandomVariate.of(distribution, 2 + random.nextInt(4));
      Tensor st1 = ScanToState.of(range);
      Tensor st2 = ScanToState.of(Reverse.of(range));
      assertEquals(Last.of(st1).negate(), Last.of(st2));
    }
  }
}
