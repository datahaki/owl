// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class ParentFileRelationTest {
  @Test
  public void testSimple() {
    assertTrue(ParentFileRelation.INSTANCE.test(new File("/some/blub"), new File("/some/blub/")));
    assertTrue(ParentFileRelation.INSTANCE.test(new File("/some/blub"), new File("/some/blub/minor")));
    assertFalse(ParentFileRelation.INSTANCE.test(new File("/some/blub/more"), new File("/some/blub/minor")));
  }
}
