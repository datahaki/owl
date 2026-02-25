// code by jph
package ch.alpine.owl.klotzki;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.MemberQ;

/* package */ record KlotskiGoalRegion(Tensor stone) implements MemberQ {
  /** Example: for Huarong Tensors.vector(0, 4, 2)
   * 
   * @param stone vector of length 3 */
  public KlotskiGoalRegion {
    VectorQ.requireLength(stone, 3);
  }

  @Override // from Region
  public boolean test(Tensor x) {
    return x.get(0).equals(stone);
  }
}
