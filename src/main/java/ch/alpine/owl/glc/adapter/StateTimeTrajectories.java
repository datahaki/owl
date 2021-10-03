// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.List;

import ch.alpine.java.lang.Lists;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Sign;

/** utility functions that operate on List<StateTime> */
public enum StateTimeTrajectories {
  ;
  /** @param glcNode
   * @param trajectory
   * @return time increment between given from State and end of trajectory
   * @throws Exception if time of last node is smaller than of given stateTime */
  public static Scalar timeIncrement(GlcNode glcNode, List<StateTime> trajectory) {
    return timeIncrement(glcNode.stateTime(), trajectory);
  }

  // helper function
  private static Scalar timeIncrement(StateTime stateTime, List<StateTime> trajectory) {
    Scalar dt = Lists.getLast(trajectory).time().subtract(stateTime.time());
    return Sign.requirePositiveOrZero(dt);
  }

  /** @param glcNode
   * @param trajectory
   * @return vector with {dt_0, dt_1, ... } all entries non-negative */
  public static Tensor deltaTimes(GlcNode glcNode, List<StateTime> trajectory) {
    Tensor dts = Tensors.reserve(trajectory.size());
    Scalar prev = glcNode.stateTime().time();
    for (StateTime stateTime : trajectory) {
      Scalar next = stateTime.time();
      dts.append(next.subtract(prev));
      prev = next;
    }
    return dts;
  }

  /** print trajectory to console
   * 
   * @param list */
  public static void print(List<StateTime> list) {
    System.out.println("Trajectory (" + list.size() + ")");
    for (StateTime stateTime : list)
      System.out.println(stateTime.toInfoString());
  }
}
