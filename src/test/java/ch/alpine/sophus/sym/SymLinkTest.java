// code by jph
package ch.alpine.sophus.sym;

import org.junit.jupiter.api.Test;

public class SymLinkTest {
  @Test
  public void testNodeNull() {
    new SymLink(null, null, null);
  }
}
