// code by astoll
package ch.alpine.owl.math.order;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.sca.Sign;

/** Implements the semiorder where the utility function is the identity mapping on the real numbers.
 * 
 * OrderComparison.STRICTLY_PRECEDES if x + slack less than y,
 * OrderComparison.STRICTLY_SUCCEDES if y + slack less than x, or
 * OrderComparison.INDIFFERENT if neither of above. */
public class ScalarSlackSemiorder implements OrderComparator<Scalar>, Serializable {
  private final Scalar slack;

  /** @param slack */
  public ScalarSlackSemiorder(Scalar slack) {
    this.slack = Sign.requirePositiveOrZero(slack); // jan added requirement later
  }

  @Override // from OrderComparator
  public OrderComparison compare(Scalar x, Scalar y) {
    if (Scalars.lessThan(x.add(slack), y))
      return OrderComparison.STRICTLY_PRECEDES;
    if (Scalars.lessThan(y.add(slack), x))
      return OrderComparison.STRICTLY_SUCCEEDS;
    return OrderComparison.INDIFFERENT;
  }
}
