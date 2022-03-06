// code by jph
package ch.alpine.sophus.demo.opt;

import java.util.stream.IntStream;

import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Times;

public enum Spearhead {
  ;
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  private static final Tensor TIP = Tensors.vector(1, 0, Math.PI);
  private static final Tensor REF = Tensors.vector(1, -1, -1);

  /** @param p
   * @param width
   * @return */
  public static Tensor of(Tensor p, Scalar width) {
    Tensor[] cp = { p, TIP, Times.of(p, REF) };
    return Tensor.of(IntStream.range(0, 3) //
        .mapToObj(index -> ClothoidTransition.of(CLOTHOID_BUILDER, cp[index], flip(cp[(index + 1) % 3])).linearized(width)) //
        .flatMap(Tensor::stream) //
        .map(Extract2D.FUNCTION));
  }

  private static Tensor flip(Tensor p) {
    Tensor q = p.copy();
    q.set(Pi.VALUE::add, 2);
    q.set(So2.MOD, 2);
    return q;
  }
}
