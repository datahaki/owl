// code by jph
package ch.alpine.owl.bot.rn.glc;

import java.awt.Dimension;
import java.util.Optional;

import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.ImageRender;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.r2.ImageCostFunction;
import ch.alpine.owl.bot.r2.WaypointDistanceCost;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.region.Regions;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2VectorCostDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(7, 6), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new R2TrajectoryControl();
    Tensor waypoints = CirclePoints.of(30).multiply(RealScalar.of(10));
    ImageCostFunction imageCostFunction = WaypointDistanceCost.of( //
        waypoints, true, RealScalar.ONE, RealScalar.of(10), new Dimension(120, 100));
    CoordinateBoundingBox range = imageCostFunction.range();
    System.out.println(range);
    Tensor image = imageCostFunction.image();
    R2Entity r2Entity = new R2VecEntity(episodeIntegrator, trajectoryControl) {
      @Override
      public Optional<CostFunction> getPrimaryCost() {
        return Optional.of(imageCostFunction);
      }
    };
    owlAnimationFrame.add(r2Entity);
    Region<Tensor> imageRegion = Regions.emptyRegion();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(imageRegion);
    MouseGoal.simple(owlAnimationFrame, r2Entity, plannerConstraint);
    owlAnimationFrame.addBackground(AxesRender.INSTANCE);
    owlAnimationFrame.addBackground(ImageRender.scale(RegionRenders.image(image), imageCostFunction.scale()));
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new R2VectorCostDemo().start().jFrame.setVisible(true);
  }
}
