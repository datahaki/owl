// code by jph, gjoel
package ch.alpine.owl.bot.rn.rrts;

import java.util.function.Supplier;

import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ class R2RrtsLetterDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    StateTime stateTime = new StateTime(Tensors.vector(6, 5), RealScalar.ZERO);
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery(region, RealScalar.of(0.05));
    R2RrtsEntity entity = new R2RrtsEntity(stateTime, transitionRegionQuery, r2ImageRegionWrap.ndBox());
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    MouseGoal.simpleRrts(owlyAnimationFrame, entity, null);
    owlyAnimationFrame.add(entity);
    Supplier<Tensor> supplier = () -> owlyAnimationFrame.geometricComponent.getMouseSe2CState();
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
          return owlyAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2RrtsLetterDemo().start().jFrame.setVisible(true);
  }
}
