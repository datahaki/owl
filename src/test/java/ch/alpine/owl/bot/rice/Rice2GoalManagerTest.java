// code by jph
package ch.alpine.owl.bot.rice;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class Rice2GoalManagerTest {
  @Test
  public void testSimple() {
    assertThrows(Exception.class, () -> new Rice2GoalManager(null));
  }
}
