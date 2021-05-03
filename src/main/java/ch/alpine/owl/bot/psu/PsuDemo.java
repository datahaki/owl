// code by jph
package ch.alpine.owl.bot.psu;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.win.OwlyGui;
import ch.alpine.owl.math.StateTimeTensorFunction;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.flow.RungeKutta4Integrator;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

/** Pendulum Swing-up
 * 
 * bapaden phd thesis: 5.5.2 Torque-Limited Pendulum Swing-Up
 * 
 * "A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning" [Paden/Frazzoli] */
/* package */ enum PsuDemo {
  ;
  private static final Tensor ETA = Tensors.vector(5, 7);

  public static TrajectoryPlanner raw(GoalInterface goalInterface) {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta4Integrator.INSTANCE, PsuStateSpaceModel.INSTANCE, RationalScalar.of(1, 4), 5);
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
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    return trajectoryPlanner;
  }

  public static TrajectoryPlanner medium() {
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, PsuStateSpaceModel.INSTANCE, RationalScalar.of(1, 4), 5);
    Collection<Tensor> controls = PsuControls.createControls(0.2, 6);
    PsuWrap psuWrap = PsuWrap.INSTANCE;
    GoalInterface goalInterface = PsuGoalManager.of( //
        PsuMetric.INSTANCE, Tensors.vector(Math.PI, 2), RealScalar.of(0.3));
    StateTimeRaster stateTimeRaster = new EtaRaster(ETA, StateTimeTensorFunction.state(psuWrap::represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    return trajectoryPlanner;
  }

  public static void main(String[] args) {
    TrajectoryPlanner trajectoryPlanner = medium();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    OwlyGui.glc(trajectoryPlanner);
  }
}
