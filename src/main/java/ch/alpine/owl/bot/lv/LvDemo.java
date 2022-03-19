// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.exp.Log;

/** the coordinates represent the population of predators and prey.
 * the domain coordinates are computed from the log of the state coordinates */
/* package */ enum LvDemo {
  ;
  public static void main(String[] args) {
    Tensor eta = Tensors.vector(10, 10);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, stateSpaceModel, RationalScalar.of(1, 30), 4);
    Collection<Tensor> controls = LvControls.create(2);
    EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
    GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
    // ---
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(Log::of));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, 0.1), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(5000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    OwlFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.addBackground(RegionRenders.create(ellipsoidRegion));
  }
}
