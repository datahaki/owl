// code by jph
package ch.alpine.owl.bot.rn.glc;

import ch.alpine.owl.bot.rn.RnPointcloudRegions;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

/* package */ class R2PointsDemo extends R2BaseDemo {
  @Override // from R2BaseDemo
  protected MemberQ region() {
    return RnPointcloudRegions.createRandomRegion(10, Tensors.vector(0, 0), Tensors.vector(4, 4), RealScalar.of(0.6));
  }

  @Override // from R2BaseDemo
  protected Tensor startState() {
    return Tensors.vector(-0.5, -0.5);
  }

  static void main() {
    new R2PointsDemo().start();
  }
}
