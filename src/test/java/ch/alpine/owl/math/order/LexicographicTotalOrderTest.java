// code by astoll
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class LexicographicTotalOrderTest {
  @SuppressWarnings("rawtypes")
  @Test
  public void testEquals() {
    List<Comparable> x = new LinkedList<>();
    x.add(1);
    x.add("zwei");
    List<Comparable> y = new LinkedList<>();
    y.add(1);
    y.add("zwei");
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(x, x), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(y, y), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(x, y), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(y, x), 0);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testLessThan() {
    List<Comparable> x = new LinkedList<>();
    x.add(true);
    x.add(3.56);
    x.add(2);
    List<Comparable> y = new LinkedList<>();
    y.add(true);
    y.add(3.56);
    y.add(3);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(x, x), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(y, y), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(x, y), -1);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(y, x), +1);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testGreaterThan() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwewwww");
    x.add(1);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    y.add(3);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(x, x), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(y, y), 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(x, y) > 0);
    assertTrue(LexicographicTotalOrder.INSTANCE.compare(y, x) < 0);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testException() {
    List<Comparable> x = new LinkedList<>();
    x.add("zwei");
    x.add(23);
    List<Comparable> y = new LinkedList<>();
    y.add("drei");
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(x, x), 0);
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(y, y), 0);
    assertThrows(Exception.class, () -> LexicographicTotalOrder.INSTANCE.compare(x, y));
    assertThrows(Exception.class, () -> LexicographicTotalOrder.INSTANCE.compare(y, x));
  }

  @Test
  public void testEmpty() {
    assertEquals(LexicographicTotalOrder.INSTANCE.compare(Arrays.asList(), Arrays.asList()), 0);
  }
}
