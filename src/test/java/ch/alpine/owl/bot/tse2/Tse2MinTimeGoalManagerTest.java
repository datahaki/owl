// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;

class Tse2MinTimeGoalManagerTest {
  @Test
  public void testSimple() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(Tensors.vector(1, 2, 3, 1), Tensors.vector(1, 1, 0.1, 1));
    Scalar rate_max = Degree.of(30); // 45
    FlowsInterface flowsInterface = Tse2CarFlows.of(rate_max, Tensors.vector(-2, 0, 2));
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, flowsInterface.getFlows(3), RealScalar.of(2));
    Scalar minCostToGoal = tse2MinTimeGoalManager.minCostToGoal(Tensors.vector(1, 13, 3, 1));
    assertTrue(Scalars.lessEquals(RealScalar.of(5), minCostToGoal));
  }

  @Test
  public void testQuantity() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 20[m], 3, 2[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 1[m*s^-1]}"));
    FlowsInterface flowsInterface = //
        Tse2CarFlows.of(Quantity.of(0.3, "m^-1"), Tensors.fromString("{-1[m*s^-2], 0[m*s^-2], 1/2[m*s^-2]}"));
    Tse2MinTimeGoalManager tse2MinTimeGoalManager = //
        new Tse2MinTimeGoalManager(tse2ComboRegion, flowsInterface.getFlows(3), Quantity.of(2, "m*s^-1"));
    Scalar minCostToGoal = tse2MinTimeGoalManager.minCostToGoal( //
        Tensors.fromString("{1[m], 13[m], 3, 5[m*s^-1]}"));
    assertTrue(Scalars.lessEquals(Quantity.of(5, "s"), minCostToGoal));
    assertTrue(Scalars.lessEquals(minCostToGoal, Quantity.of(6, "s")));
  }
}
