// code by jph
package ch.alpine.owl.math.state;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.itp.LinearBinaryAverage;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** wrapper around trajectory for fast search and control query
 * 
 * the wrap executes get function at a given time
 * the get functions lookup the {@link TrajectorySample}
 * strictly greater than the given time */
public class TrajectoryWrap {
  public static TrajectoryWrap of(List<TrajectorySample> trajectory) {
    return new TrajectoryWrap(trajectory);
  }
  // ---

  /** unmodifiable */
  private final List<TrajectorySample> trajectory;
  private final NavigableMap<Scalar, TrajectorySample> navigableMap;
  private final Clip clip;

  /** @param trajectory non-empty */
  private TrajectoryWrap(List<TrajectorySample> trajectory) {
    this.trajectory = Collections.unmodifiableList(trajectory);
    navigableMap = trajectory.stream().collect(Collectors.toMap( //
        trajectorySample -> trajectorySample.stateTime().time(), //
        Function.identity(), (u, v) -> null, TreeMap::new));
    clip = Clips.keycover(navigableMap);
  }

  /** @return unmodifiable list */
  public List<TrajectorySample> trajectory() {
    return trajectory;
  }

  public Clip getClip() {
    return clip;
  }

  /** @param now
   * @return true if trajectory defines control value now or in the future */
  public boolean isRelevant(Scalar now) {
    return Scalars.lessThan(now, clip.max());
  }

  /** @param now
   * @return true, if given now is in semi-open interval */
  public boolean isDefined(Scalar now) {
    return Scalars.lessEquals(clip.min(), now) && isRelevant(now);
  }

  /** @param now
   * @return control to reach trajectory sample registered at time strictly greater than given now
   * @throws Exception if {@link #isDefined(Scalar)} returns false for given now */
  public Tensor getControl(Scalar now) {
    return navigableMap.higherEntry(now).getValue().getFlow().orElseThrow();
  }

  /** @param now
   * @return empty if now is outside of time defined by trajectory
   * @throws Exception if {@link #isDefined(Scalar)} returns false for given now */
  public TrajectorySample getSample(Scalar now) {
    Entry<Scalar, TrajectorySample> lo = navigableMap.floorEntry(now);
    Entry<Scalar, TrajectorySample> hi = navigableMap.higherEntry(now);
    Scalar index = Clips.interval(lo.getKey(), hi.getKey()).rescale(now);
    Tensor split = LinearBinaryAverage.INSTANCE.split( //
        lo.getValue().stateTime().state(), //
        hi.getValue().stateTime().state(), index);
    return new TrajectorySample( //
        new StateTime(split, now), //
        hi.getValue().getFlow().orElseThrow());
  }
}
