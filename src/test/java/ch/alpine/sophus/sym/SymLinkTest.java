// code by jph
package ch.alpine.sophus.sym;

import junit.framework.TestCase;

public class SymLinkTest extends TestCase {
  public void testNodeNull() {
    new SymLink(null, null, null);
  }
}
