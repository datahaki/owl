// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.r2.R2xTImageStateTimeRegion;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.api.Region;
import ch.alpine.sophis.flow.StateSpaceModels;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.sophis.math.bij.R2RigidFamily;
import ch.alpine.sophis.math.bij.Se2Family;
import ch.alpine.sophis.reg.ImageRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTImageAnimationDemo implements DemoInterface {
  private static final Scalar DELAY = Quantity.of(1.5, "s");

  @Override
  public OwlAnimationFrame getTimerFrame() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        StateSpaceModels.SINGLE_INTEGRATOR, //
        TimeIntegrators.EULER, //
        new StateTime(Tensors.vector(1.5, 2), Quantity.of(0, "s")));
    TrajectoryEntity abstractEntity = new R2xTEntity(episodeIntegrator, DELAY);
    owlAnimationFrame.add(abstractEntity);
    R2RigidFamily rigidFamily = Se2Family.rotationAround( //
        Tensors.vectorDouble(1.5, 2), time -> time.multiply(RealScalar.of(0.1)));
    ImageRegion imageRegion = R2ImageRegions.inside_circ();
    Region<StateTime> region = new R2xTImageStateTimeRegion( //
        imageRegion, rigidFamily, () -> abstractEntity.getStateTimeNow().time());
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(region);
    MouseGoal.simple(owlAnimationFrame, abstractEntity, plannerConstraint);
    owlAnimationFrame.addBackground((RenderInterface) region);
    // owlyAnimationFrame.addBackground(new CurveRender());
    owlAnimationFrame.geometricComponent.setOffset(200, 400);
    return owlAnimationFrame;
  }

  static void main() {
    new R2xTImageAnimationDemo().runStandalone();
  }
}
