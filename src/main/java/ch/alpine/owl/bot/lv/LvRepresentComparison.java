// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeTensorFunction;
import ch.alpine.owl.region.EllipsoidRegion;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.exp.Log;

/** the coordinates represent the population of predators and prey.
 * the domain coordinates are computed from the log of the state coordinates */
/* package */ enum LvRepresentComparison {
  ;
  static void launch(TensorUnaryOperator represent) {
    Tensor eta = Tensors.vector(10, 10);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, stateSpaceModel, Rational.of(1, 30), 4);
    Collection<Tensor> controls = LvControls.create(2);
    EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
    GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
    // ---
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(represent));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, .5), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(4000);
    OwlFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.addBackground(RegionRenders.create(ellipsoidRegion));
    owlFrame.geometricComponent.setOffset(100, 300);
    owlFrame.jFrame.setBounds(100, 100, 500, 500);
  }

  static void main() {
    launch(tensor -> tensor.maps(Log.FUNCTION));
    launch(t -> t);
  }
}
