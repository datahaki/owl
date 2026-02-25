// code by jph
package ch.alpine.owl.klotzki;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

class KlotskiGoalAdapterTest {
  @Test
  void testSimple() {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      KlotskiGoalAdapter klotskiGoalAdapter = KlotskiGoalAdapter.of(klotskiProblem.goal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.startState());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }

  @Test
  void testPennant() {
    for (Pennant pennant : Pennant.values()) {
      KlotskiProblem klotskiProblem = pennant.create();
      KlotskiGoalAdapter klotskiGoalAdapter = KlotskiGoalAdapter.of(klotskiProblem.goal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.startState());
      assertEquals(minCostToGoal, RealScalar.of(3));
    }
  }

  @Test
  void testTrafficJam() {
    for (TrafficJam trafficJam : TrafficJam.values()) {
      KlotskiProblem klotskiProblem = trafficJam.create();
      KlotskiGoalAdapter klotskiGoalAdapter = KlotskiGoalAdapter.of(klotskiProblem.goal());
      Scalar minCostToGoal = klotskiGoalAdapter.minCostToGoal(klotskiProblem.startState());
      assertEquals(minCostToGoal, RealScalar.of(7));
    }
  }
}
