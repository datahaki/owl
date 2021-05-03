// code by jph
package ch.alpine.owl.data;

import ch.alpine.tensor.Scalars;
import junit.framework.TestCase;

public class TimeKeeperTest extends TestCase {
  public void testSimple() {
    TimeKeeper tk = new TimeKeeper();
    assertTrue(Scalars.nonZero(tk.now()));
  }
}
