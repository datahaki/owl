// code by jph, gjoel
package ch.alpine.owl.bot.rn.rrts;

import java.util.function.Supplier;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

/* package */ class R2RrtsLetterDemo implements DemoInterface {
  @Override
  public TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    MemberQ region = r2ImageRegionWrap.region();
    StateTime stateTime = new StateTime(Tensors.vector(6, 5), RealScalar.ZERO);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery(region, RealScalar.of(0.05));
    R2RrtsEntity entity = new R2RrtsEntity(stateTime, transitionRegionQuery, r2ImageRegionWrap.coordinateBounds());
    owlAnimationFrame.addBackground(RegionRenderFactory.create(region));
    MouseGoal.simpleRrts(owlAnimationFrame.timerFrame.geometricComponent(), entity, null);
    owlAnimationFrame.add(entity);
    Supplier<Tensor> supplier = () -> owlAnimationFrame.timerFrame.geometricComponent().getMouseSe2CState();
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), region)), //
          R2RrtsEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return entity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.timerFrame.geometricComponent().getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
    owlAnimationFrame.timerFrame.setTitle(getClass().getSimpleName());
    owlAnimationFrame.timerFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame.timerFrame;
  }

  static void main() {
    new R2RrtsLetterDemo().runStandalone();
  }
}
