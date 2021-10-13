// code by jph
package ch.alpine.owl.bot.sat;

import java.util.Collection;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum SatelliteDemo {
  ;
  public static void main(String[] args) throws Exception {
    Collection<Tensor> controls = new SatelliteControls(RealScalar.of(0.9)).getFlows(6);
    Tensor start = Tensors.vector(2, 0, 0, 2); // pos, vel
    Region<Tensor> obstacleRegion = new EllipsoidRegion( // obstacle at origin
        Tensors.vector(0, 0, 0, 0), //
        Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    // ---
    Tensor eta = Tensors.vector(3, 3, 2, 2);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, SatelliteStateSpaceModel.INSTANCE, RationalScalar.of(1, 10), 6);
    PlannerConstraint plannerConstraint = //
        new TrajectoryObstacleConstraint(CatchyTrajectoryRegionQuery.timeInvariant(obstacleRegion));
    EllipsoidRegion goalRegion = new EllipsoidRegion( //
        Tensors.vector(2, -2, 0, 0), Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    GoalInterface goalInterface = new SatelliteGoalManager(goalRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(start, RealScalar.ZERO));
    // ---
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.addBackground(RegionRenders.create(obstacleRegion));
    owlFrame.addBackground(RegionRenders.create(goalRegion));
    // ---
    owlFrame.jFrame.setBounds(100, 100, 600, 600);
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    while (!glcExpand.isOptimal() && owlFrame.jFrame.isVisible()) {
      glcExpand.findAny(50);
      owlFrame.setGlc(trajectoryPlanner);
      Thread.sleep(1);
    }
    System.out.println("#expand = " + glcExpand.getExpandCount());
  }
}
