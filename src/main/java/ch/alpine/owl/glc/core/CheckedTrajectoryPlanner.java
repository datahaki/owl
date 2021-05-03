// code by jph
package ch.alpine.owl.glc.core;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;

/** Hint: only use for debug/test purposes */
public class CheckedTrajectoryPlanner implements TrajectoryPlanner {
  public static TrajectoryPlanner wrap(TrajectoryPlanner trajectoryPlanner) {
    return new CheckedTrajectoryPlanner(trajectoryPlanner);
  }

  // ---
  private final TrajectoryPlanner trajectoryPlanner;

  private CheckedTrajectoryPlanner(TrajectoryPlanner trajectoryPlanner) {
    this.trajectoryPlanner = Objects.requireNonNull(trajectoryPlanner);
  }

  @Override
  public Optional<GlcNode> pollNext() {
    return trajectoryPlanner.pollNext();
  }

  @Override
  public void expand(GlcNode glcNode) {
    trajectoryPlanner.expand(glcNode);
  }

  @Override
  public Optional<GlcNode> getBest() {
    return trajectoryPlanner.getBest();
  }

  @Override
  public void insertRoot(StateTime stateTime) {
    if (getBestOrElsePeek().isPresent())
      throw new RuntimeException();
    trajectoryPlanner.insertRoot(stateTime);
    if (!getBestOrElsePeek().isPresent())
      throw new RuntimeException();
  }

  @Override
  public Optional<GlcNode> getBestOrElsePeek() {
    return Objects.requireNonNull(trajectoryPlanner.getBestOrElsePeek());
  }

  @Override
  public StateIntegrator getStateIntegrator() {
    return Objects.requireNonNull(trajectoryPlanner.getStateIntegrator());
  }

  @Override
  public HeuristicFunction getHeuristicFunction() {
    return Objects.requireNonNull(trajectoryPlanner.getHeuristicFunction());
  }

  @Override
  public Map<Tensor, GlcNode> getDomainMap() {
    return Objects.requireNonNull(trajectoryPlanner.getDomainMap());
  }

  @Override
  public Collection<GlcNode> getQueue() {
    return Objects.requireNonNull(trajectoryPlanner.getQueue());
  }
}
