// code by jph
package ch.alpine.java.win;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

public class TimerFrameTest {
  @Test
  public void testSimple() {
    long convert = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);
    assertEquals(convert, 1000);
  }
}
