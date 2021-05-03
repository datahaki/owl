// code by jph
package ch.alpine.owl.bot.kl;

import junit.framework.TestCase;

public class KlotskiObstacleRegionTest extends TestCase {
  public void testHuarong() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.startState()));
    }
  }

  public void testPennant() {
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.startState()));
    }
  }

  public void testSolomon() {
    for (Solomon solomon : Solomon.values()) {
      KlotskiProblem klotskiProblem = solomon.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.startState()));
    }
  }

  public void testTrafficJam() {
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.startState()));
    }
  }

  public void testSunshine() {
    for (Sunshine sunshine : Sunshine.values()) {
      KlotskiProblem klotskiProblem = sunshine.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).isMember(klotskiProblem.startState()));
    }
  }
}
