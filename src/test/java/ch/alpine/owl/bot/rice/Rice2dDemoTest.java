// code by jph
package ch.alpine.owl.bot.rice;

import java.util.List;

import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.adapter.GlcTrajectories;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GlcNodes;
import ch.alpine.owl.glc.core.HeuristicAssert;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.gui.ren.TrajectoryRender;
import ch.alpine.owl.gui.win.OwlyFrame;
import ch.alpine.owl.gui.win.OwlyGui;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Timing;
import junit.framework.TestCase;

public class Rice2dDemoTest extends TestCase {
  public void testExpand() throws InterruptedException {
    Scalar mu = RealScalar.of(-.5);
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    TrajectoryPlanner trajectoryPlanner = Rice2dDemo.createInstance(mu, stateSpaceModel);
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000); // 153 0.368319228
    assertTrue(timing.seconds() < 1.5);
    assertTrue(glcExpand.getExpandCount() < 500);
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    GlcNodes.getPathFromRootTo(glcNode);
    List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, stateSpaceModel, RationalScalar.HALF, 5), glcNode);
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(samples);
    owlyFrame.addBackground(trajectoryRender);
    owlyFrame.addBackground(RegionRenders.create(Rice2dDemo.ELLIPSOID_REGION));
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    Thread.sleep(120);
    owlyFrame.jFrame.setVisible(false);
  }

  public void testGlcExpand() throws InterruptedException {
    Scalar mu = RealScalar.of(-.5);
    StateSpaceModel stateSpaceModel = Rice2StateSpaceModel.of(mu);
    TrajectoryPlanner trajectoryPlanner = Rice2dDemo.createInstance(mu, stateSpaceModel);
    Timing timing = Timing.started();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.untilOptimal(1000); // 220 0.283809941
    assertTrue(timing.seconds() < 1.5);
    assertTrue(glcExpand.getExpandCount() < 500);
    OwlyFrame owlyFrame = OwlyGui.glc(trajectoryPlanner);
    GlcNode glcNode = trajectoryPlanner.getBest().get();
    GlcNodes.getPathFromRootTo(glcNode);
    List<TrajectorySample> samples = GlcTrajectories.detailedTrajectoryTo(FixedStateIntegrator.create( //
        MidpointIntegrator.INSTANCE, stateSpaceModel, RationalScalar.HALF, 5), glcNode);
    TrajectoryRender trajectoryRender = new TrajectoryRender();
    trajectoryRender.trajectory(samples);
    owlyFrame.addBackground(trajectoryRender);
    owlyFrame.addBackground(RegionRenders.create(Rice2dDemo.ELLIPSOID_REGION));
    HeuristicAssert.check(trajectoryPlanner);
    // TrajectoryPlannerConsistency.check(trajectoryPlanner);
    Thread.sleep(120);
    owlyFrame.jFrame.setVisible(false);
  }
}
