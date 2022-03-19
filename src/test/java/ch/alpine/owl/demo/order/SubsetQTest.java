// code by jph
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class SubsetQTest {
  @Test
  public void testSimple() {
    assertTrue(SubsetQ.of(Arrays.asList(1, 2, 3), Arrays.asList(3, 1)));
  }
}
