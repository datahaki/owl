// code by astoll
package ch.alpine.owl.bot.ap;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class ApWrapTest extends TestCase {
  public void testWrap() {
    double pa = 2 * Math.PI;
    Tensor toBeTested = Tensors.vector(100, 20, -30, 6.6);
    Tensor expected = Tensors.vector(100, 20, -30, 6.6 - pa);
    Chop._12.requireClose(expected, ApWrap.INSTANCE.represent(toBeTested));
    Chop._14.requireAllZero(ApWrap.INSTANCE.difference(expected, toBeTested));
  }
}
