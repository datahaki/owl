// code by jph
package ch.alpine.owl.data;

import java.util.Arrays;
import java.util.LinkedList;

import ch.alpine.java.lang.Lists;
import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class ListsTest extends TestCase {
  public void testSimple() {
    assertEquals(Lists.getLast(Arrays.asList(3, 2, 8)), (Integer) 8);
  }

  public void testFail() {
    AssertFail.of(() -> Lists.getLast(new LinkedList<>()));
  }
}
