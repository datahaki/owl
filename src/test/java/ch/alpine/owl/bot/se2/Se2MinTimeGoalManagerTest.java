// code by jph
package ch.alpine.owl.bot.se2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.glc.Se2CarFlows;
import ch.alpine.owl.bot.se2.twd.TwdDuckieFlows;
import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.HeuristicQ;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class Se2MinTimeGoalManagerTest {
  @Test
  public void testIsMember() {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Collection<Tensor> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.test(new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(3))));
    assertFalse(goalInterface.test(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.of(3))));
    assertFalse(goalInterface.test(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.of(3))));
  }

  @Test
  public void testGoalAdapter() {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Collection<Tensor> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.test(new StateTime(Tensors.vector(1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.test(new StateTime(Tensors.vector(-1, 2, 3), RealScalar.ZERO)));
    assertFalse(goalInterface.test(new StateTime(Tensors.vector(1, 2, 3.2), RealScalar.ZERO)));
  }

  @Test
  public void testQuantity() {
    FlowsInterface carFlows = Se2CarFlows.standard(Quantity.of(1, "m*s^-1"), Quantity.of(0.5, "m^-1"));
    Collection<Tensor> controls = carFlows.getFlows(3);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball( //
        Tensors.fromString("{1[m], 2[m], 3}"), //
        Tensors.fromString("{1[m], 1[m], 0.1}"));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    GoalInterface goalInterface = se2MinTimeGoalManager.getGoalInterface();
    assertTrue(goalInterface.test(new StateTime(Tensors.fromString("{1[m], 2[m], 3}"), RealScalar.ZERO)));
    assertFalse(goalInterface.test(new StateTime(Tensors.fromString("{-1[m], 2[m], 3}"), RealScalar.ZERO)));
    assertFalse(goalInterface.test(new StateTime(Tensors.fromString("{1[m], 2[m], 3.2}"), RealScalar.ZERO)));
    {
      Scalar minCostToGoal = goalInterface.minCostToGoal(Tensors.fromString("{1[m], 2[m], 3.2}"));
      Chop._10.requireClose(Quantity.of(0.2, "s"), minCostToGoal);
    }
    {
      Scalar minCostToGoal = goalInterface.minCostToGoal(Tensors.fromString("{15[m], 22[m], 3.1}"));
      Chop._10.requireClose(Quantity.of(23.413111231467404, "s"), minCostToGoal);
    }
  }

  @Test
  public void testSimple() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(1), RealScalar.of(1));
    Collection<Tensor> controls = twdConfig.getFlows(8);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(10, 0, Math.PI), Tensors.vector(1, 1, 1));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    assertTrue(HeuristicQ.of(se2MinTimeGoalManager));
    Scalar cost = se2MinTimeGoalManager.minCostToGoal(Tensors.vector(0, 0, 0));
    assertTrue(Scalars.lessEquals(RealScalar.of(9), cost));
    assertTrue(se2MinTimeGoalManager.test(Tensors.vector(10, 0, Math.PI + 0.9)));
    assertFalse(se2MinTimeGoalManager.test(Tensors.vector(10, 0, Math.PI + 1.1)));
    assertTrue(se2MinTimeGoalManager.test(Tensors.vector(10, 0, Math.PI + 2 * Math.PI + 0.9)));
    assertFalse(se2MinTimeGoalManager.test(Tensors.vector(10, 0, Math.PI + 2 * Math.PI + 1.1)));
  }

  @Test
  public void testAllAngles() {
    TwdDuckieFlows twdConfig = new TwdDuckieFlows(RealScalar.of(1), RealScalar.of(1));
    Collection<Tensor> controls = twdConfig.getFlows(8);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(0, 0, Math.PI), Tensors.vector(1, 1, 4));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    for (int index = -100; index < 100; ++index) {
      assertTrue(se2MinTimeGoalManager.test(Tensors.vector(0, 0, index)));
      assertFalse(se2MinTimeGoalManager.test(Tensors.vector(2, 0, index)));
    }
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    FlowsInterface carFlows = Se2CarFlows.standard(RealScalar.ONE, RealScalar.ONE);
    Serialization.copy(carFlows);
    Collection<Tensor> controls = carFlows.getFlows(3);
    Serialization.copy(controls);
    Se2ComboRegion se2ComboRegion = Se2ComboRegion.ball(Tensors.vector(1, 2, 3), Tensors.vector(1, 1, 0.1));
    Serialization.copy(se2ComboRegion);
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    Serialization.copy(se2MinTimeGoalManager);
  }

  @Test
  public void testWrapSuccessor() {
    FlowsInterface carFlows = Se2CarFlows.forward(RealScalar.ONE, Degree.of(10));
    Collection<Tensor> controls = carFlows.getFlows(6);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion( //
        new BallRegion(Tensors.vector(-0.5, 0), RealScalar.of(0.3)), //
        So2Region.covering(RealScalar.of(2), RealScalar.ONE));
    Se2MinTimeGoalManager se2MinTimeGoalManager = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    //
    assertEquals(se2MinTimeGoalManager.minCostToGoal(Tensors.vector(-0.5, 0, 2)), RealScalar.ZERO);
    assertEquals(se2MinTimeGoalManager.minCostToGoal(Tensors.vector(-0.5, 0, 2 + 10)), RealScalar.of(51.56620156177409));
    assertEquals(se2MinTimeGoalManager.minCostToGoal(Tensors.vector(-0.5, 2, 2 + 10)), RealScalar.of(51.56620156177409));
    assertEquals(se2MinTimeGoalManager.minCostToGoal(Tensors.vector(-0.5, 2, 2)), RealScalar.of(1.7));
    //
    GlcNode glcNode = GlcNodes.createRoot(new StateTime(Tensors.vector(1, 2, 0), Quantity.of(3, "s")), se2MinTimeGoalManager.getGoalInterface());
    Scalar scalar = se2MinTimeGoalManager.costIncrement( //
        glcNode, //
        Arrays.asList(new StateTime(Tensors.vector(1, 2, 3), Quantity.of(10, "s"))), //
        null);
    assertEquals(scalar, Quantity.of(7, "s"));
    assertTrue(ExactScalarQ.of(scalar));
  }
}
