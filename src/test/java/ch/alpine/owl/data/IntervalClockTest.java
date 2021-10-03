// code by jph
package ch.alpine.owl.data;

import ch.alpine.java.win.IntervalClock;
import junit.framework.TestCase;

public class IntervalClockTest extends TestCase {
  public void testHertz() {
    IntervalClock intervalClock = new IntervalClock();
    double hertz = intervalClock.hertz();
    assertTrue(100 < hertz);
  }

  public void testSeconds() {
    IntervalClock intervalClock = new IntervalClock();
    double seconds = intervalClock.seconds();
    assertTrue(seconds < 0.01);
  }
}
