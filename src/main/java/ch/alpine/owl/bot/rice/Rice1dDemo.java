// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.glc.adapter.EtaRaster;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.adapter.StateTimeTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.region.EllipsoidRegion;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;

/** position and velocity control in 1D with friction
 * 
 * References:
 * "Mobility and Autonomous Reconfiguration of Marsokhod" */
/* package */ enum Rice1dDemo {
  ;
  public static TrajectoryPlanner simple() {
    Tensor eta = Tensors.vector(8, 8);
    Scalar mu = RealScalar.of(-0.5);
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, stateSpaceModel, Rational.of(1, 8), 5);
    Collection<Tensor> controls = Rice2Controls.create1d(15); //
    GoalInterface goalInterface = new Rice1GoalManager(new EllipsoidRegion(Tensors.vector(6, -.7), Tensors.vector(0.4, 0.3)));
    MemberQ region1 = new EllipsoidRegion(Tensors.vector(+3, +1), Tensors.vector(1.75, 0.75));
    MemberQ region2 = new EllipsoidRegion(Tensors.vector(-2, +0), Tensors.vector(1, 1));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant( //
        MemberQ.any(List.of( //
            region1, // speed limit along the way
            region2 // block to the left
        )));
    StateTimeRaster stateTimeRaster = EtaRaster.state(eta);
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        stateTimeRaster, stateIntegrator, controls, plannerConstraint, goalInterface);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    if (900 < glcExpand.getExpandCount())
      System.out.println("close to upper bound: " + glcExpand.getExpandCount());
    return trajectoryPlanner;
  }

  static void main() {
    TrajectoryPlanner trajectoryPlanner = simple();
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.orElseThrow());
      StateTimeTrajectories.print(trajectory);
    }
    OwlGui.glc(trajectoryPlanner);
  }
}
