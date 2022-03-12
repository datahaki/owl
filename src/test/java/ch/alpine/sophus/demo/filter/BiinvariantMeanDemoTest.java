// code by jph
package ch.alpine.sophus.demo.filter;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class BiinvariantMeanDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BiinvariantMeanDemo());
  }
}
