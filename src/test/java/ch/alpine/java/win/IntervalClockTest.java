// code by jph
package ch.alpine.java.win;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IntervalClockTest {
  @Test
  public void testHertz() {
    IntervalClock intervalClock = new IntervalClock();
    double hertz = intervalClock.hertz();
    assertTrue(100 < hertz);
  }

  @Test
  public void testSeconds() {
    IntervalClock intervalClock = new IntervalClock();
    double seconds = intervalClock.seconds();
    assertTrue(seconds < 0.01);
  }
}
