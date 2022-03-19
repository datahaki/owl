// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Optional;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.data.tree.Expand;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.rrts.DefaultRrtsPlannerServer;
import ch.alpine.owl.rrts.RrtsPlannerServer;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

/* package */ enum Se2RrtsPlannerServerDemo {
  ;
  public static void main(String[] args) throws Exception {
    Tensor range = Tensors.vector(7, 7).unmodifiable();
    Region<Tensor> imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", range, false);
    Tensor lbounds = Array.zeros(2).unmodifiable();
    Tensor ubounds = range.unmodifiable();
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.05));
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    // ---
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        transitionSpace, //
        transitionRegionQuery, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return new Se2RrtsNodeCollection(transitionSpace, CoordinateBounds.of(lbounds, ubounds), 3);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return randomSampleInterface;
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return BallRandomSample.of(goal, RealScalar.ONE);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return CarRrtsFlow.uBetween(orig, dest);
      }
    };
    // ---
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(60, 477);
    owlFrame.jFrame.setBounds(100, 100, 550, 550);
    owlFrame.addBackground(RegionRenders.create(imageRegion));
    StateTime stateTime = new StateTime(Append.of(lbounds, RealScalar.ZERO), RealScalar.ZERO);
    Tensor goal = RandomSample.of(randomSampleInterface);
    Tensor trajectory = Tensors.empty();
    int frame = 0;
    while (frame++ < 5 && owlFrame.jFrame.isVisible()) {
      server.setGoal(goal);
      server.insertRoot(stateTime);
      server.setState(stateTime);
      new Expand<>(server).steps(200);
      owlFrame.setRrts(transitionSpace, server.getRoot().get(), transitionRegionQuery);
      Optional<List<TrajectorySample>> optional = server.getTrajectory();
      if (optional.isPresent()) {
        optional.get().stream().map(TrajectorySample::stateTime).map(StateTime::state).forEach(trajectory::append);
        owlFrame.geometricComponent.addRenderInterface(new RenderInterface() {
          @Override
          public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
            Path2D path = geometricLayer.toPath2D(trajectory);
            graphics.setStroke(new BasicStroke(2));
            graphics.setColor(Color.BLACK);
            graphics.draw(path);
          }
        });
        owlFrame.geometricComponent.jComponent.repaint();
        // ---
        stateTime = Lists.last(optional.get()).stateTime();
        goal = RandomSample.of(randomSampleInterface);
      }
      System.out.println(frame + "/" + 5);
      Thread.sleep(10);
    }
  }
}
