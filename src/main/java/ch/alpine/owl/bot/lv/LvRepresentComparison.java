// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.StateTimeRaster;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.StateTimeTensorFunction;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.exp.Log;

/** the coordinates represent the population of predators and prey.
 * the domain coordinates are computed from the log of the state coordinates */
/* package */ enum LvRepresentComparison {
  ;
  static void launch(TensorUnaryOperator represent) {
    Tensor eta = Tensors.vector(10, 10);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.EXAMPLE;
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        TimeIntegrators.RK45, stateSpaceModel, Quantity.of(Rational.of(1, 30), "s"), 4);
    Collection<Tensor> controls = LvControls.create(Quantity.of(1.0, "s^-1"), 2);
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
    TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(ellipsoidRegion));
    owlFrame.geometricComponent.setOffset(100, 300);
    owlFrame.jFrame.setBounds(100, 100, 500, 500);
  }

  static void main() {
    launch(tensor -> tensor.maps(Log.FUNCTION));
    launch(t -> t);
  }
}
