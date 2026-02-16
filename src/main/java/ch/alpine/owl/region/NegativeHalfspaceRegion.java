// code by jph
package ch.alpine.owl.region;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.sca.Sign;

/** open region consisting of all states with a negative coordinate at a given index
 * 
 * region in R^n */
public class NegativeHalfspaceRegion implements MemberQ {
  private final int index;

  /** @param index of state coordinate that when negative is inside the region */
  public NegativeHalfspaceRegion(int index) {
    this.index = index;
  }

  @Override // from Region
  public boolean test(Tensor tensor) {
    return Sign.isNegative(tensor.Get(index));
  }
}
