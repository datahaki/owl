// code by jph
package ch.alpine.owl.bot.sat;

import java.util.Collection;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.flow.RungeKutta45Integrator;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

/* package */ enum SatelliteDemo {
  ;
  static void main() throws Exception {
    Collection<Tensor> controls = new SatelliteControls(RealScalar.of(0.9)).getFlows(6);
    Tensor start = Tensors.vector(2, 0, 0, 2); // pos, vel
    MemberQ obstacleRegion = new EllipsoidRegion( // obstacle at origin
        Tensors.vector(0, 0, 0, 0), //
        Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    // ---
    Tensor eta = Tensors.vector(3, 3, 2, 2);
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        RungeKutta45Integrator.INSTANCE, SatelliteStateSpaceModel.INSTANCE, Quantity.of(Rational.of(1, 10), "s"), 6);
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(obstacleRegion));
    EllipsoidRegion goalRegion = new EllipsoidRegion( //
        Tensors.vector(2, -2, 0, 0), Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    GoalInterface goalInterface = new SatelliteGoalManager(goalRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(start, RealScalar.ZERO));
    // ---
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!glcExpand.isOptimal()) {
      glcExpand.findAny(50);
      Thread.sleep(1);
    }
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(obstacleRegion));
    owlFrame.geometricComponent.addRenderInterface(RegionRenders.create(goalRegion));
    // ---
    owlFrame.jFrame.setBounds(100, 100, 600, 600);
    System.out.println("#expand = " + glcExpand.getExpandCount());
  }
}
