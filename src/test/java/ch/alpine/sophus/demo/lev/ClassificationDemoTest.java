// code by jph
package ch.alpine.sophus.demo.lev;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClassificationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClassificationDemo());
  }
}
