// code by gjoel
package ch.alpine.owl.rrts;

import java.util.List;

import ch.alpine.owl.bot.rn.RnTransitionSpace;
import ch.alpine.owl.bot.rn.rrts.RnRrtsFlow;
import ch.alpine.owl.bot.rn.rrts.RnRrtsNodeCollection;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.se2.rrts.CarRrtsFlow;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.alpine.owl.bot.se2.rrts.DubinsTransitionSpace;
import ch.alpine.owl.bot.se2.rrts.Se2RrtsNodeCollections;
import ch.alpine.owl.data.Lists;
import ch.alpine.owl.data.tree.Expand;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.core.RrtsNodeCollection;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.ConstantRandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class DefaultRrtsPlannerServerTest extends TestCase {
  public void testRn() {
    Tensor goal = Tensors.vector(10, 10);
    Tensor state = Tensors.vector(0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    Scalar radius = Vector2Norm.between(goal, state).multiply(RationalScalar.HALF).add(RealScalar.ONE);
    Tensor center = Mean.of(Tensors.of(state, goal));
    NdBox ndBox = NdBox.of( //
        center.map(scalar -> scalar.subtract(radius)), //
        center.map(scalar -> scalar.add(radius)));
    // ---
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        RnTransitionSpace.INSTANCE, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return new RnRrtsNodeCollection(ndBox);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BallRandomSample.of(center, radius);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return new ConstantRandomSample(goal);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return RnRrtsFlow.uBetween(orig, dest);
      }
    };
    server.setGoal(goal);
    server.insertRoot(stateTime);
    new Expand<>(server).steps(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._05.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }

  public void testDubins() {
    NdBox ndBox = NdBox.of( //
        Tensors.vector(0, 0, -Math.PI), //
        Tensors.vector(10, 10, Math.PI));
    Tensor goal = Tensors.vector(10, 10, 0);
    Tensor state = Tensors.vector(0, 0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    // ---
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH), //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return new RnRrtsNodeCollection(ndBox);
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return BoxRandomSample.of(ndBox);
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return new ConstantRandomSample(goal);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return CarRrtsFlow.uBetween(orig, dest);
      }
    };
    server.setGoal(goal);
    server.insertRoot(stateTime);
    new Expand<>(server).steps(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._05.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }

  public void testClothoid() {
    Tensor lbounds = Tensors.vector(0, 0);
    Tensor ubounds = Tensors.vector(10, 10);
    Tensor goal = Tensors.vector(10, 10, 0);
    Tensor state = Tensors.vector(0, 0, 0);
    StateTime stateTime = new StateTime(state, RealScalar.ZERO);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        Append.of(lbounds, Pi.VALUE.negate()), //
        Append.of(ubounds, Pi.VALUE));
    // ---
    RrtsPlannerServer server = new DefaultRrtsPlannerServer( //
        ClothoidTransitionSpace.ANALYTIC, //
        EmptyTransitionRegionQuery.INSTANCE, //
        RationalScalar.of(1, 10), //
        Se2StateSpaceModel.INSTANCE, //
        LengthCostFunction.INSTANCE) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        return Se2RrtsNodeCollections.of(getTransitionSpace(), NdBox.of(lbounds, ubounds));
      }

      @Override
      protected RandomSampleInterface spaceSampler(Tensor state) {
        return randomSampleInterface;
      }

      @Override
      protected RandomSampleInterface goalSampler(Tensor goal) {
        return new ConstantRandomSample(goal);
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return CarRrtsFlow.uBetween(orig, dest);
      }
    };
    server.setGoal(goal);
    server.insertRoot(stateTime);
    new Expand<>(server).steps(400);
    // ---
    assertTrue(server.getTrajectory().isPresent());
    List<TrajectorySample> trajectory = server.getTrajectory().get();
    Chop._05.requireClose(goal, Lists.getLast(trajectory).stateTime().state());
  }
}
