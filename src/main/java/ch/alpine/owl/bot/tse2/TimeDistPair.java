// code by jph
package ch.alpine.owl.bot.tse2;

import ch.alpine.tensor.Scalar;

/* package */ class TimeDistPair {
  public final Scalar time;
  public final Scalar dist;

  public TimeDistPair(Scalar time, Scalar dist) {
    this.time = time;
    this.dist = dist;
  }

  @Override
  public String toString() {
    return String.format("[%s,%s]", time, dist);
  }
}