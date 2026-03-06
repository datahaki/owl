// code by jph
package ch.alpine.owl.bot.psu;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.StateTimeTensorFunction;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.qty.Quantity;

/** Pendulum Swing-up
 * 
 * bapaden phd thesis: 5.5.2 Torque-Limited Pendulum Swing-Up
 * 
 * "A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning" [Paden/Frazzoli] */
/* package */ enum PsuDemo {
  ;
  private static final Tensor ETA = Tensors.vector(5, 7);

  public static TrajectoryPlanner raw(GoalInterface goalInterface) {
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        TimeIntegrators.RK4, PsuStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 30), "s"), 5);
    Collection<Tensor> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    StateTimeRaster stateTimeRaster = new EtaRaster(ETA, StateTimeTensorFunction.state(psuWrap::represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    return trajectoryPlanner;
  }

  public static TrajectoryPlanner simple() {
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI * 0.7, 0.5), RealScalar.of(0.3));
    TrajectoryPlanner trajectoryPlanner = raw(goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), Quantity.of(0,"s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    return trajectoryPlanner;
  }

  public static TrajectoryPlanner medium() {
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        TimeIntegrators.RK45, PsuStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 4), "s"), 5);
    Collection<Tensor> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI, 2), RealScalar.of(0.3));
    StateTimeRaster stateTimeRaster = new EtaRaster(ETA, StateTimeTensorFunction.state(psuWrap::represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), Quantity.of(0,"s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    return trajectoryPlanner;
  }

  static void main() {
    TrajectoryPlanner trajectoryPlanner = medium();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    OwlGui.glc(trajectoryPlanner);
  }
}
