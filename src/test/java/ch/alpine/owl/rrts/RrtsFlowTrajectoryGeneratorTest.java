// code by gjoel
package ch.alpine.owl.rrts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.rn.rrts.RnRrtsFlow;
import ch.alpine.owl.bot.rn.rrts.RnRrtsNodeCollection;
import ch.alpine.owl.bot.se2.Se2FlowIntegrator;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.se2.rrts.CarRrtsFlow;
import ch.alpine.owl.bot.se2.rrts.Se2RrtsNodeCollection;
import ch.alpine.owl.data.tree.Nodes;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.rrts.adapter.DirectionalTransitionSpace;
import ch.alpine.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.LengthCostFunction;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.sophus.crv.clt.ClothoidTransitionSpace;
import ch.alpine.sophus.crv.dub.DubinsPathComparators;
import ch.alpine.sophus.crv.dub.DubinsTransitionSpace;
import ch.alpine.sophus.lie.rn.RnTransitionSpace;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;

class RrtsFlowTrajectoryGeneratorTest {
  @Test
  void testRn() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        new RnRrtsNodeCollection(CoordinateBounds.of(Tensors.vector(-5, -5), Tensors.vector(10, 10))), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 0), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 0), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        RnRrtsFlow::uBetween);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(RnTransitionSpace.INSTANCE, sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    assertEquals(31, trajectory.size());
    for (int i = 1; i < 31; i++) {
      TrajectorySample sample = trajectory.get(i);
      assertEquals(RationalScalar.of(i, 10), sample.stateTime().time());
      assertEquals(Tensors.of(sample.stateTime().time(), RealScalar.ZERO), sample.stateTime().state());
      assertEquals(Tensors.vector(1, 0), sample.getFlow().get());
    }
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 31).stream().map(TrajectorySample::getFlow).allMatch(Optional::isPresent));
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop._15.requireClose(n3.state(), trajectory.getLast().stateTime().state());
  }

  @Test
  void testDubins() {
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH);
    Rrts rrts = new DefaultRrts( //
        transitionSpace, //
        new Se2RrtsNodeCollection(transitionSpace, CoordinateBounds.of(Tensors.vector(-5, -5), Tensors.vector(10, 10)), 3), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 0, 0), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 1, Math.PI / 2), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        Se2StateSpaceModel.INSTANCE, //
        CarRrtsFlow::uBetween);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH), sequence, RealScalar.ZERO, RationalScalar.of(1, 10));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(37, trajectory.size());
    for (int i = 1; i < 21; i++) {
      TrajectorySample sample = trajectory.get(i);
      Chop._15.requireClose(sample.stateTime().time(), RationalScalar.of(i, 10));
      Chop._15.requireClose(sample.stateTime().state(), Tensors.of(sample.stateTime().time(), RealScalar.ZERO, RealScalar.ZERO));
      Chop._14.requireClose(sample.getFlow().get(), Tensors.vector(1, 0, 0));
    }
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    Chop._15.requireClose(n1.state(), trajectory.get(10).stateTime().state());
    Chop._15.requireClose(n2.state(), trajectory.get(20).stateTime().state());
    Chop._15.requireClose(n3.state(), trajectory.getLast().stateTime().state());
    // ---
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 37).stream().map(TrajectorySample::getFlow).allMatch(Optional::isPresent));
    Iterator<TrajectorySample> iterator = trajectory.iterator();
    EpisodeIntegrator integrator = new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, //
        Se2FlowIntegrator.INSTANCE, //
        iterator.next().stateTime());
    while (iterator.hasNext()) {
      TrajectorySample trajectorySample = iterator.next();
      integrator.move(trajectorySample.getFlow().get(), trajectorySample.stateTime().time());
      assertEquals(trajectorySample.stateTime().time(), integrator.tail().time());
      Chop._03.requireClose(trajectorySample.stateTime().state(), integrator.tail().state());
    }
  }

  @Test
  void testClothoid() {
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    Rrts rrts = new DefaultRrts( //
        transitionSpace, //
        new Se2RrtsNodeCollection(transitionSpace, CoordinateBounds.of(Tensors.vector(-5, -5), Tensors.vector(10, 10)), 3), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 0, 0), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 2, Math.PI / 2), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        Se2StateSpaceModel.INSTANCE, //
        CarRrtsFlow::uBetween);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(ClothoidTransitionSpace.ANALYTIC, sequence, RealScalar.ZERO, RationalScalar.of(1, 16));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(71, trajectory.size());
    for (int i = 1; i < 33; i++) {
      // TrajectorySample sample = trajectory.get(i);
      // Tolerance.CHOP.requireClose(RationalScalar.of(i, 16), sample.stateTime().time());
      // assertEquals(Tensors.of(sample.stateTime().time(), RealScalar.ZERO, RealScalar.ZERO), sample.stateTime().state());
      // assertEquals(Tensors.vector(1, 0, 0), sample.getFlow().get());
    }
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    // Chop._15.requireClose(n1.state(), trajectory.get(16).stateTime().state());
    // Chop._15.requireClose(n2.state(), trajectory.get(32).stateTime().state());
    Chop._15.requireClose(n3.state(), trajectory.getLast().stateTime().state());
    // ---
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 44).stream().map(TrajectorySample::getFlow).map(Optional::get) //
        .allMatch(u -> u.Get(1).equals(RealScalar.ZERO)));
    Iterator<TrajectorySample> iterator = trajectory.iterator();
    EpisodeIntegrator integrator = new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, //
        Se2FlowIntegrator.INSTANCE, //
        iterator.next().stateTime());
    while (iterator.hasNext()) {
      TrajectorySample trajectorySample = iterator.next();
      integrator.move(trajectorySample.getFlow().get(), trajectorySample.stateTime().time());
      assertEquals(trajectorySample.stateTime().time(), integrator.tail().time());
      // Chop._03.requireClose(trajectorySample.stateTime().state(), integrator.tail().state());
    }
  }

  @Test
  void testDirectionalClothoid() {
    TransitionSpace transitionSpace = DirectionalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC);
    Rrts rrts = new DefaultRrts( //
        transitionSpace, //
        // no specific collection for directional clothoid
        new Se2RrtsNodeCollection(transitionSpace, CoordinateBounds.of(Tensors.vector(-5, -5), Tensors.vector(10, 10)), 3), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    RrtsNode root = rrts.insertAsNode(Tensors.vector(0, 0, 0), 0).get();
    assertEquals(0, root.children().size());
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0, 0), 0).get();
    assertEquals(1, root.children().size());
    /* RrtsNode n_1 = */ rrts.insertAsNode(Tensors.vector(-1, 0, 0), 0).get();
    assertEquals(2, root.children().size());
    RrtsNode n2 = rrts.insertAsNode(Tensors.vector(2, 1, Math.PI / 2), 0).get();
    assertEquals(1, n1.children().size());
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 0, Math.PI), 0).get();
    assertEquals(1, n2.children().size());
    // ---
    List<RrtsNode> sequence = Nodes.listFromRoot(n3);
    assertEquals(sequence, Arrays.asList(root, n1, n2, n3));
    RrtsFlowTrajectoryGenerator generator = new RrtsFlowTrajectoryGenerator( //
        Se2StateSpaceModel.INSTANCE, //
        CarRrtsFlow::uBetween);
    List<TrajectorySample> trajectory = //
        generator.createTrajectory(DirectionalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC), sequence, RealScalar.ZERO, RationalScalar.of(1, 16));
    // trajectory.stream().map(TrajectorySample::toInfoString).forEach(System.out::println);
    assertEquals(67, trajectory.size());
    for (int i = 1; i < 17; i++) {
      // TrajectorySample sample = trajectory.get(i);
      // assertEquals(RationalScalar.of(i, 16), sample.stateTime().time());
      // assertEquals(Tensors.of(sample.stateTime().time(), RealScalar.ZERO, RealScalar.ZERO), sample.stateTime().state());
      // assertTrue(Chop._15.close(sample.getFlow().get(), Tensors.vector(1, 0, 0)));
    }
    Chop._15.requireClose(root.state(), trajectory.get(0).stateTime().state());
    // Chop._15.requireClose(n1.state(), trajectory.get(16).stateTime().state());
    // Chop._15.requireClose(n2.state(), trajectory.get(32).stateTime().state());
    Chop._15.requireClose(n3.state(), trajectory.getLast().stateTime().state());
    // ---
    assertFalse(trajectory.get(0).getFlow().isPresent());
    assertTrue(trajectory.subList(1, 49).stream().map(TrajectorySample::getFlow).allMatch(Optional::isPresent));
    // assertTrue(trajectory.subList(1, 33).stream().map(TrajectorySample::getFlow).map(Optional::get).allMatch(t -> Sign.isPositive(t.Get(0))));
    // assertTrue(trajectory.subList(33, 49).stream().map(TrajectorySample::getFlow).map(Optional::get).allMatch(t -> Sign.isNegative(t.Get(0))));
    Iterator<TrajectorySample> iterator = trajectory.iterator();
    EpisodeIntegrator integrator = new SimpleEpisodeIntegrator(Se2StateSpaceModel.INSTANCE, //
        Se2FlowIntegrator.INSTANCE, //
        iterator.next().stateTime());
    while (iterator.hasNext()) {
      TrajectorySample trajectorySample = iterator.next();
      integrator.move(trajectorySample.getFlow().get(), trajectorySample.stateTime().time());
      assertEquals(trajectorySample.stateTime().time(), integrator.tail().time());
      try {
        Chop._03.requireClose(trajectorySample.stateTime().state(), integrator.tail().state());
      } catch (Throw e) { // +/- close to pi
        Chop._03.requireClose(trajectorySample.stateTime().state().extract(0, 2), integrator.tail().state().extract(0, 2));
        Chop._03.requireClose( //
            Abs.FUNCTION.apply(trajectorySample.stateTime().state().Get(2)), //
            Abs.FUNCTION.apply(integrator.tail().state().Get(2)));
      }
    }
  }
}
