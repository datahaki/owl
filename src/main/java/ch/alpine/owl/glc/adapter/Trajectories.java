// code by jph
package ch.alpine.owl.glc.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Lists;

public enum Trajectories {
  ;
  /** @param head
   * @param tail
   * @return trajectory { head[0:end], tail[1:end]}
   * @throws Exception if head[end] != tail[0] */
  public static List<TrajectorySample> glue(List<TrajectorySample> head, List<TrajectorySample> tail) {
    List<TrajectorySample> trajectory = new ArrayList<>(head);
    TrajectorySample tsh = head.getLast();
    TrajectorySample tst = tail.get(0);
    boolean contact = tsh.stateTime().equals(tst.stateTime());
    if (!contact) {
      System.err.println("last of head: " + tsh.toInfoString());
      System.err.println(" 1st of tail: " + tst.toInfoString());
      // GlobalAssert.that(contact);
    }
    if (tst.getFlow().isPresent())
      throw new RuntimeException();
    trajectory.addAll(Lists.rest(tail));
    return Collections.unmodifiableList(trajectory);
  }

  /** @param list */
  public static void print(List<TrajectorySample> list) {
    System.out.println("Trajectory (" + list.size() + ")");
    for (TrajectorySample sample : list)
      System.out.println(sample.toInfoString());
  }

  /** @param time
   * @return predicate for {@link TrajectorySample} before or at given time */
  public static Predicate<TrajectorySample> untilTime(Scalar time) {
    return trajectorySample -> Scalars.lessEquals(trajectorySample.stateTime().time(), time);
  }

  /** @param time
   * @return predicate for {@link TrajectorySample} after or at given time */
  public static Predicate<TrajectorySample> afterTime(Scalar time) {
    return trajectorySample -> Scalars.lessEquals(time, trajectorySample.stateTime().time());
  }
}
