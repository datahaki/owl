// code by jph
package ch.alpine.owl.bot.tn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.alpine.owl.math.CoordinateWrap;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Mod;

class TnWrap implements CoordinateWrap, Serializable {
  private static final Scalar NEGATIVE_HALF = Rational.HALF.negate();
  // ---
  private final Tensor extension;
  private final List<Mod> mod_distance = new ArrayList<>();

  /** @param extension of torus along each axis */
  public TnWrap(Tensor extension) {
    this.extension = extension;
    for (Tensor _n : extension) {
      Scalar n = (Scalar) _n;
      mod_distance.add(Mod.function(n, n.multiply(NEGATIVE_HALF)));
    }
  }

  @Override // from CoordinateWrap
  public Tensor represent(Tensor x) {
    return Tensors.vector(i -> Mod.function(extension.Get(i)).apply(x.Get(i)), x.length());
  }

  @Override // from TensorDifference
  public Tensor difference(Tensor p, Tensor q) {
    Tensor d = p.subtract(q);
    return Tensors.vector(i -> mod_distance.get(i).apply(d.Get(i)), d.length());
  }
}
