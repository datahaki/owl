// code by jph
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class IntegerLog2Test {
  @Test
  public void testFloor() {
    assertEquals(IntegerLog2.floor(1), 0);
    assertEquals(IntegerLog2.floor(2), 1);
    assertEquals(IntegerLog2.floor(3), 1);
    assertEquals(IntegerLog2.floor(4), 2);
    assertEquals(IntegerLog2.floor(5), 2);
    assertEquals(IntegerLog2.floor(1024 + 123), 10);
  }

  @Test
  public void testCeil() {
    assertEquals(IntegerLog2.ceiling(1), 0);
    assertEquals(IntegerLog2.ceiling(2), 1);
    assertEquals(IntegerLog2.ceiling(3), 2);
    assertEquals(IntegerLog2.ceiling(4), 2);
    assertEquals(IntegerLog2.ceiling(5), 3);
    assertEquals(IntegerLog2.ceiling(1024 + 123), 11);
  }

  @Test
  public void testFailZero() {
    AssertFail.of(() -> IntegerLog2.ceiling(0));
  }

  @Test
  public void testFailNegative() {
    AssertFail.of(() -> IntegerLog2.ceiling(-1));
  }
}
