// code by jph
package ch.alpine.owl.ani.api;

import java.util.List;

import ch.alpine.owlets.math.state.TrajectorySample;

@FunctionalInterface
public interface TrajectoryListener {
  /** @param trajectory */
  void trajectory(List<TrajectorySample> trajectory);
}
