// code by jph
package ch.alpine.owl.bot.kl;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class KlotskiObstacleRegionTest {
  @Test
  void testHuarong() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).test(klotskiProblem.startState()));
    }
  }

  @Test
  void testPennant() {
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).test(klotskiProblem.startState()));
    }
  }

  @Test
  void testSolomon() {
    for (Solomon solomon : Solomon.values()) {
      KlotskiProblem klotskiProblem = solomon.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).test(klotskiProblem.startState()));
    }
  }

  @Test
  void testTrafficJam() {
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).test(klotskiProblem.startState()));
    }
  }

  @Test
  void testSunshine() {
    for (Sunshine sunshine : Sunshine.values()) {
      KlotskiProblem klotskiProblem = sunshine.create();
      assertFalse(KlotskiObstacleRegion.fromSize(klotskiProblem.size()).test(klotskiProblem.startState()));
    }
  }
}
