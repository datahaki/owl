// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.awt.Dimension;
import java.util.Optional;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageCostFunction;
import ch.alpine.owl.bot.r2.WaypointDistanceCost;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.MouseGoal;
import ch.alpine.owl.gui.region.ImageRender;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.Regions;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.CirclePoints;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2VectorCostDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlyAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(7, 6), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    Tensor waypoints = CirclePoints.of(30).multiply(RealScalar.of(10));
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(10), new Dimension(120, 100));
    Tensor range = imageCostFunction.range();
    System.out.println(range);
    Tensor image = imageCostFunction.image();
    R2Entity r2Entity = new R2VecEntity(episodeIntegrator, trajectoryControl) {
      @Override
      public Optional<CostFunction> getPrimaryCost() {
        return Optional.of(imageCostFunction);
      }
    };
    owlyAnimationFrame.add(r2Entity);
    Region<Tensor> imageRegion = Regions.emptyRegion();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(imageRegion);
    MouseGoal.simple(owlyAnimationFrame, r2Entity, plannerConstraint);
    owlyAnimationFrame.addBackground(AxesRender.INSTANCE);
    owlyAnimationFrame.addBackground(ImageRender.scale(RegionRenders.image(image), imageCostFunction.scale()));
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2VectorCostDemo().start().jFrame.setVisible(true);
  }
}
