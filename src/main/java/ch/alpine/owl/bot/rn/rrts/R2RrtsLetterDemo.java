// code by jph, gjoel
package ch.alpine.owl.bot.rn.rrts;

import java.util.function.Supplier;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ class R2RrtsLetterDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    StateTime stateTime = new StateTime(Tensors.vector(6, 5), RealScalar.ZERO);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery(region, RealScalar.of(0.05));
    R2RrtsEntity entity = new R2RrtsEntity(stateTime, transitionRegionQuery, r2ImageRegionWrap.coordinateBounds());
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simpleRrts(owlAnimationFrame, entity, null);
    owlAnimationFrame.add(entity);
    Supplier<Tensor> supplier = () -> owlAnimationFrame.geometricComponent.getMouseSe2CState();
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
          return owlAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2RrtsLetterDemo().start().jFrame.setVisible(true);
  }
}
