// code by jph
package ch.alpine.owl.bot.tse2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;

public class Tse2ForwardMinTimeGoalManagerTest {
  @Test
  public void testSimple() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 0[m], 0, 4[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 4[m*s^-1]}"));
    Clip v_range = tse2ComboRegion.v_range();
    assertEquals(v_range.min(), Quantity.of(0, "m*s^-1"));
    assertEquals(v_range.max(), Quantity.of(8, "m*s^-1"));
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.fromString("{-1[m*s^-2], 0[m*s^-2], 1[m*s^-2]}"));
    Collection<Tensor> controls = flowsInterface.getFlows(1);
    Tse2ForwardMinTimeGoalManager tse2ForwardMinTimeGoalManager = //
        new Tse2ForwardMinTimeGoalManager(tse2ComboRegion, controls);
    {
      Scalar minCostToGoal = tse2ForwardMinTimeGoalManager.minCostToGoal(Tensors.fromString("{0[m], 0[m], 0, 0[m*s^-1]}"));
      Chop._10.requireClose(minCostToGoal, Quantity.of(4.242640687119285, "s"));
    }
    {
      Scalar minCostToGoal = tse2ForwardMinTimeGoalManager.minCostToGoal(Tensors.fromString("{0[m], 0[m], 0, 6[m*s^-1]}"));
      Chop._10.requireClose(minCostToGoal, Quantity.of(1.3484692283495345, "s"));
    }
    assertTrue(tse2ForwardMinTimeGoalManager.test(Tensors.fromString("{10[m], 0[m], 0, 4[m*s^-1]}")));
    assertTrue(tse2ForwardMinTimeGoalManager.test(Tensors.fromString("{11[m], 0[m], 0, 4[m*s^-1]}")));
    assertTrue(tse2ForwardMinTimeGoalManager.test(Tensors.fromString("{10[m], 0[m], 0, 0[m*s^-1]}")));
    assertTrue(tse2ForwardMinTimeGoalManager.test(Tensors.fromString("{10[m], 0[m], 0, 8[m*s^-1]}")));
    assertFalse(tse2ForwardMinTimeGoalManager.test(Tensors.fromString("{10[m], 0[m], 0, 9[m*s^-1]}")));
  }

  @Test
  public void testFail() {
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical( //
        Tensors.fromString("{10[m], 0[m], 0, 4[m*s^-1]}"), //
        Tensors.fromString("{1[m], 1[m], 1, 5[m*s^-1]}"));
    FlowsInterface flowsInterface = Tse2CarFlows.of(Quantity.of(1, "m^-1"), Tensors.fromString("{-1[m*s^-2], 0[m*s^-2], 1[m*s^-2]}"));
    Collection<Tensor> controls = flowsInterface.getFlows(1);
    AssertFail.of(() -> new Tse2ForwardMinTimeGoalManager(tse2ComboRegion, controls));
  }
}
