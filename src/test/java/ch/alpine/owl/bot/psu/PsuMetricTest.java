// code by jph
package ch.alpine.owl.bot.psu;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class PsuMetricTest extends TestCase {
  public void testPeriod() {
    Chop._12.requireZero(PsuMetric.INSTANCE.distance(Tensors.vector(0, 0), Tensors.vector(2 * Math.PI, 0)));
    Chop._12.requireZero(PsuMetric.INSTANCE.distance(Tensors.vector(0, 0), Tensors.vector(4 * Math.PI, 0)));
  }

  public void testLinear() {
    assertEquals(PsuMetric.INSTANCE.distance(Tensors.vector(0, 0), Tensors.vector(0, 10)), RealScalar.of(10));
    assertEquals(PsuMetric.INSTANCE.distance(Tensors.vector(0, -10), Tensors.vector(0, 10)), RealScalar.of(20));
  }
}
