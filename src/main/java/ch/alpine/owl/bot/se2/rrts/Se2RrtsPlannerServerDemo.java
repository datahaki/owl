// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.util.List;
import java.util.Optional;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.data.tree.Expand;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.owlets.math.state.TrajectorySample;
import ch.alpine.owlets.rrts.DefaultRrtsPlannerServer;
import ch.alpine.owlets.rrts.RrtsPlannerServer;
import ch.alpine.owlets.rrts.adapter.LengthCostFunction;
import ch.alpine.owlets.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owlets.rrts.core.RrtsNodeCollection;
import ch.alpine.owlets.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ts.ClothoidTransitionSpace;
import ch.alpine.sophis.ts.TransitionSpace;
import ch.alpine.sophus.rsm.BallRandomSample;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

/* package */ enum Se2RrtsPlannerServerDemo {
  ;
  static void main() throws Exception {
    Tensor range = Tensors.vector(7, 7).unmodifiable();
    MemberQ imageRegion = //
        ImageRegions.loadFromRepository("/io/track0_100.png", range, false);
    Tensor lbounds = Array.zeros(2).unmodifiable();
    Tensor ubounds = range.unmodifiable();
    TransitionRegionQuery transitionRegionQuery = new SampledTransitionRegionQuery( //
        imageRegion, RealScalar.of(0.05));
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    // ---
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE)));
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        transitionSpace, //
        transitionRegionQuery, //
        Rational.of(1, 10), //
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
    StateTime stateTime = new StateTime(Append.of(lbounds, RealScalar.ZERO), RealScalar.ZERO);
    Tensor goal = RandomSample.of(randomSampleInterface);
    Tensor trajectory = Tensors.empty();
    int frame = 0;
    while (frame++ < 5) {
      server.setGoal(goal);
      server.insertRoot(stateTime);
      server.setState(stateTime);
      new Expand<>(server).steps(200);
      Optional<List<TrajectorySample>> optional = server.getTrajectory();
      if (optional.isPresent()) {
        optional.get().stream().map(TrajectorySample::stateTime).map(StateTime::state).forEach(trajectory::append);
        // owlFrame.geometricComponent.addRenderInterface(new RenderInterface() {
        // @Override
        // public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        // Path2D path = geometricLayer.toPath2D(trajectory);
        // graphics.setStroke(new BasicStroke(2));
        // graphics.setColor(Color.BLACK);
        // graphics.draw(path);
        // }
        // });
        // ---
        stateTime = optional.get().getLast().stateTime();
        goal = RandomSample.of(randomSampleInterface);
      }
      System.out.println(frame + "/" + 5);
      Thread.sleep(10);
    }
    TimerFrame timerFrame = OwlGui.rrts(transitionSpace, server.getRoot().get(), transitionRegionQuery);
    timerFrame.setBounds(100, 100, 550, 550);
    timerFrame.geometricComponent().addRenderInterfaceBackground(RegionRenderFactory.create(imageRegion));
  }
}
