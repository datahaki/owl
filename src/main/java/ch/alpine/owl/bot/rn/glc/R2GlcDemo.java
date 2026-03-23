// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.awt.Container;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.ren.GridRender;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.owl.bot.r2.R2Bubbles;
import ch.alpine.owl.bot.r2.R2Flows;
import ch.alpine.owl.bot.rn.RnMinDistGoalManager;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.glc.adapter.EtaRaster;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.adapter.StateTimeTrajectories;
import ch.alpine.owlets.glc.core.GlcNode;
import ch.alpine.owlets.glc.core.GlcNodes;
import ch.alpine.owlets.glc.core.GoalInterface;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owlets.math.state.FixedStateIntegrator;
import ch.alpine.owlets.math.state.StateIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.crv.d2.ex.Box2D;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Ramp;

/** functionality is used in tests */
@ReflectionMarker
enum R2GlcDemo implements ManipulateProvider {
  EMPTY {
    @Override
    PlannerConstraint plannerConstraint() {
      return EmptyPlannerConstraint.INSTANCE;
    }
  },
  BUBBLES {
    @Override
    PlannerConstraint plannerConstraint() {
      return RegionConstraints.timeInvariant(R2Bubbles.INSTANCE);
    }
  };

  private final StateIntegrator STATE_INTEGRATOR = new FixedStateIntegrator( //
      TimeIntegrators.EULER, //
      StateSpaceModels.SINGLE_INTEGRATOR, //
      Quantity.of(Rational.of(1, 5), "s"), //
      5);
  private final GeometricComponent geometricComponent = new GeometricComponent();

  private R2GlcDemo() {
    Tensor stateRoot = Tensors.vector(-2, -2);
    Tensor stateGoal = Tensors.vector(2, 2);
    Scalar radius = RealScalar.of(0.25);
    Tensor eta = Tensors.vector(8, 8);
    R2Flows r2Flows = new R2Flows(Quantity.of(1, "s^-1"));
    Collection<Tensor> controls = r2Flows.getFlows(36);
    BallRegion ballRegion = new BallRegion(stateGoal, radius);
    GoalInterface goalInterface = new RnMinDistGoalManager(ballRegion);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        EtaRaster.state(eta), STATE_INTEGRATOR, controls, plannerConstraint(), goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(stateRoot, Quantity.of(0, "s")));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(200);
    {
      Optional<GlcNode> optional = trajectoryPlanner.getBest();
      if (optional.isPresent()) {
        GlcNode goalNode = optional.orElseThrow(); // <- throws exception if
        Scalar cost = goalNode.costFromRoot();
        Scalar lowerBound = Ramp.FUNCTION.apply(Vector2Norm.between(stateGoal, stateRoot).subtract(radius));
        if (Scalars.lessThan(cost, lowerBound))
          throw new Throw(cost, lowerBound);
      }
    }
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    geometricComponent.addRenderInterfaceBackground(new GridRender(geometricComponent::getSize));
    CoordinateBoundingBox cbb = Box2D.xy(Clips.absolute(3));
    geometricComponent.addRenderInterfaceBackground(new SignedDistRender(cbb, 100, R2Bubbles.INSTANCE));
    RenderElements.create(trajectoryPlanner).forEach(geometricComponent::addRenderInterface);
    Tensor pvm = PvmBuilder.rhs().setOffset(300, 300).setPerPixel(100).digest();
    geometricComponent.setModel2Pixel(pvm);
  }

  abstract PlannerConstraint plannerConstraint();

  @Override
  public Container getContainer() {
    return geometricComponent;
  }

  static void main() {
    EMPTY.runStandalone();
    BUBBLES.runStandalone();
  }
}
