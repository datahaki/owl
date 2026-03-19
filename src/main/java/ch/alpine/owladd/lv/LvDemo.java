// code by jph
package ch.alpine.owladd.lv;

import java.awt.Container;
import java.util.Collection;

import ch.alpine.ascony.ren.GridRender;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.ren.RenderElements;
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
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.exp.Log;

/** the coordinates represent the population of predators and prey.
 * the domain coordinates are computed from the log of the state coordinates */
@ReflectionMarker
class LvDemo implements ManipulateProvider {
  public TimeIntegrators integrator = TimeIntegrators.RK4;
  @Override
  public Container getContainer() {
    Tensor eta = Tensors.vector(10, 10);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.EXAMPLE;
    StateIntegrator stateIntegrator = new FixedStateIntegrator( //
        integrator, stateSpaceModel, Quantity.of(Rational.of(1, 30), "s"), 4);
    Collection<Tensor> controls = LvControls.create(Quantity.of(1.0, "s^-1"), 2);
    EllipsoidRegion ellipsoidRegion = new EllipsoidRegion(Tensors.vector(2, 1), Tensors.vector(0.1, 0.1));
    GoalInterface goalInterface = new LvGoalInterface(ellipsoidRegion);
    // ---
    StateTimeRaster stateTimeRaster = new EtaRaster(eta, StateTimeTensorFunction.state(tensor -> tensor.maps(Log.FUNCTION)));
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, EmptyPlannerConstraint.INSTANCE, goalInterface);
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Tensors.vector(2, 0.5), Quantity.of(0, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(5000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    GeometricComponent geometricComponent = new GeometricComponent();
    RenderElements.create(trajectoryPlanner) //
        .forEach(geometricComponent::addRenderInterface);
    geometricComponent.addRenderInterfaceBackground(new GridRender(geometricComponent::getSize));
    geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(ellipsoidRegion));
    Tensor digest = PvmBuilder.rhs().setOffset(50, 680).setPerPixel(20).digest();
    geometricComponent.setModel2Pixel(digest);
    return geometricComponent;
  }

  static void main() {
    new LvDemo().runStandalone();
  }
}
