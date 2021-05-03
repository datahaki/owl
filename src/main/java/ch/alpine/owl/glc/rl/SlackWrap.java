// code by jph, yn
package ch.alpine.owl.glc.rl;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Sign;

/* package */ class SlackWrap {
  private final Tensor slack;

  public SlackWrap(Tensor slack) {
    this.slack = slack;
  }

  // TODO_YN name is not good, more like: "isBetter" ?
  public boolean isWithin(Tensor merit, Tensor entrywiseMin) {
    Tensor diff = entrywiseMin.add(slack).subtract(merit);
    // old: diff.stream().map(Scalar.class::cast).allMatch(Sign::isPositiveOrZero);
    return diff.stream().map(Scalar.class::cast).noneMatch(Sign::isNegative);
  }
}
