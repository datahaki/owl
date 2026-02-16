// code by jph
package ch.alpine.owl.bot.rice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.GlcTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.ren.TrajectoryRender;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Timing;

class Rice2dDemoTest {
  @Test
  void testExpand() throws InterruptedException {
    Scalar mu = RealScalar.of(-.5);
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    TrajectoryPlanner trajectoryPlanner = Rice2dDemo.createInstance(mu, stateSpaceModel);
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000); // 153 0.368319228
    assertTrue(Scalars.lessThan(timing.seconds(), Quantity.of(1.5, "s")));
    assertTrue(glcExpand.getExpandCount() < 500);
    OwlFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    GlcNodes.getPathFromRootTo(glcNode);
    List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, stateSpaceModel, Rational.HALF, 5), glcNode);
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(samples);
    owlFrame.addBackground(trajectoryRender);
    owlFrame.addBackground(RegionRenders.create(Rice2dDemo.ELLIPSOID_REGION));
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    Thread.sleep(120);
    owlFrame.jFrame.setVisible(false);
  }

  @Test
  void testGlcExpand() throws InterruptedException {
    Scalar mu = RealScalar.of(-.5);
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    TrajectoryPlanner trajectoryPlanner = Rice2dDemo.createInstance(mu, stateSpaceModel);
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.untilOptimal(1000); // 220 0.283809941
    assertTrue(Scalars.lessThan(timing.seconds(), Quantity.of(1.5, "s")));
    assertTrue(glcExpand.getExpandCount() < 500);
    OwlFrame owlFrame = OwlGui.glc(trajectoryPlanner);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    GlcNodes.getPathFromRootTo(glcNode);
    List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, stateSpaceModel, Rational.HALF, 5), glcNode);
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(samples);
    owlFrame.addBackground(trajectoryRender);
    owlFrame.addBackground(RegionRenders.create(Rice2dDemo.ELLIPSOID_REGION));
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    Thread.sleep(120);
    owlFrame.jFrame.setVisible(false);
  }
}
