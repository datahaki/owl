// code by jph, gjoel
package ch.alpine.owl.rrts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import ch.alpine.owl.math.IntegerLog2;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.rrts.adapter.DirectedTransition;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.crv.Transition;
import ch.alpine.sophus.crv.TransitionSpace;
import ch.alpine.sophus.crv.TransitionWrap;
import ch.alpine.sophus.math.AdjacentDistances;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sign;

/* package */ class RrtsFlowTrajectoryGenerator {
  private final StateSpaceModel stateSpaceModel;
  private final BiFunction<StateTime, StateTime, Tensor> uBetween;
  private CurveSubdivision curveSubdivision = null;
  private TensorMetric tensorMetric = null;

  public RrtsFlowTrajectoryGenerator( //
      StateSpaceModel stateSpaceModel, //
      BiFunction<StateTime, StateTime, Tensor> uBetween) {
    this.stateSpaceModel = stateSpaceModel;
    this.uBetween = uBetween;
  }

  /** @param curveSubdivision interpolation scheme
   * @param tensorMetric distance metric between samples */
  public void addPostProcessing(CurveSubdivision curveSubdivision, TensorMetric tensorMetric) {
    this.curveSubdivision = Objects.requireNonNull(curveSubdivision);
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
  }

  /** @param transitionSpace
   * @param sequence of control point nodes
   * @param dt minimal time resolution
   * @return trajectory */
  public List<TrajectorySample> createTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    if (Objects.isNull(curveSubdivision))
      return standardTrajectory(transitionSpace, sequence, t0, dt);
    return postProcessedTrajectory(transitionSpace, sequence, t0, dt);
  }

  private List<TrajectorySample> standardTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    List<TrajectorySample> trajectory = new LinkedList<>();
    RrtsNode prev = sequence.get(0);
    trajectory.add(TrajectorySample.head(new StateTime(prev.state(), t0)));
    for (RrtsNode node : Lists.rest(sequence)) {
      Transition transition = transitionSpace.connect(prev.state(), node.state());
      TransitionWrap transitionWrap = transition.wrapped(dt);
      Tensor samples = transitionWrap.samples();
      Tensor spacing = transitionWrap.spacing();
      Scalar ti = t0;
      for (int i = 0; i < samples.length(); i++) {
        ti = ti.add(spacing.Get(i));
        StateTime stateTime = new StateTime(samples.get(i), ti);
        StateTime orig = Lists.last(trajectory).stateTime();
        // TODO OWL ALG this boolean expression appears twice => extract to function
        Tensor u = (transition instanceof DirectedTransition directedTransition && !directedTransition.isForward) //
            ? uBetween.apply(stateTime, orig) //
            : uBetween.apply(orig, stateTime);
        trajectory.add(new TrajectorySample(stateTime, u));
      }
      prev = node;
      t0 = t0.add(transition.length());
    }
    return trajectory;
  }

  private List<TrajectorySample> postProcessedTrajectory( //
      TransitionSpace transitionSpace, List<RrtsNode> sequence, Scalar t0, final Scalar dt) {
    List<TrajectorySample> trajectory = new LinkedList<>();
    Iterator<RrtsNode> iterator = sequence.iterator();
    RrtsNode prev = iterator.next();
    trajectory.add(TrajectorySample.head(new StateTime(prev.state(), t0)));
    boolean prevDirection = true;
    List<RrtsNode> segment = new ArrayList<>();
    while (iterator.hasNext()) {
      RrtsNode node = iterator.next();
      Transition transition = transitionSpace.connect(prev.state(), node.state());
      boolean direction = (!(transition instanceof DirectedTransition directedTransition)) || directedTransition.isForward;
      if (direction != prevDirection) {
        flush(transitionSpace, trajectory, segment, prevDirection, dt);
        prevDirection = direction;
        segment = new ArrayList<>();
      }
      if (segment.isEmpty())
        segment.add(prev);
      segment.add(node);
      prev = node;
    }
    return flush(transitionSpace, trajectory, segment, prevDirection, dt);
  }

  private List<TrajectorySample> flush(TransitionSpace transitionSpace, List<TrajectorySample> trajectory, List<RrtsNode> segment, boolean direction,
      Scalar dt) {
    if (!segment.isEmpty()) {
      Tensor points = Tensor.of(segment.stream().map(RrtsNode::state));
      Scalar maxLength = segment.stream().skip(1) //
          .map(node -> transitionSpace.connect(node.parent().state(), node.state()).length()).reduce(Max::of).get();
      Scalar t0 = Lists.last(trajectory).stateTime().time();
      int depth = IntegerLog2.ceiling(Ceiling.of(maxLength.divide(Sign.requirePositive(dt))).number().intValue());
      if (!direction)
        points = Reverse.of(points);
      Tensor samples = Nest.of(curveSubdivision::string, points, depth);
      Tensor spacing = new AdjacentDistances(tensorMetric).apply(samples);
      if (!direction) {
        samples = Reverse.of(samples);
        spacing = Reverse.of(spacing);
      }
      Scalar ti = t0;
      for (int i = 1; i < samples.length(); i++) {
        ti = ti.add(spacing.Get(i - 1));
        StateTime stateTime = new StateTime(samples.get(i), ti);
        StateTime orig = Lists.last(trajectory).stateTime();
        Tensor u = direction //
            ? uBetween.apply(orig, stateTime) //
            : uBetween.apply(stateTime, orig);
        trajectory.add(new TrajectorySample(stateTime, u));
      }
    }
    return trajectory;
  }
}
