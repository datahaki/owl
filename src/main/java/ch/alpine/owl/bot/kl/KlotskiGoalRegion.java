// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.owl.math.region.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;

/* package */ class KlotskiGoalRegion implements Region<Tensor> {
  private final Tensor stone;

  /** Example: for Huarong Tensors.vector(0, 4, 2)
   * 
   * @param stone vector of length 3 */
  public KlotskiGoalRegion(Tensor stone) {
    this.stone = VectorQ.requireLength(stone, 3);
  }

  @Override // from Region
  public boolean isMember(Tensor x) {
    return x.get(0).equals(stone);
  }
}
