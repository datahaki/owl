// code by jph
package ch.alpine.sophus.app.sym;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;

/* package */ class SymWeights {
  public static Tensor of(SymScalar root) {
    return new SymWeights(root).vector();
  }

  // ---
  private final Tensor sum = Array.zeros(0);
  private int max = 0;

  private SymWeights(SymScalar root) {
    visit(RealScalar.ONE, root);
  }

  private void visit(Scalar weight, SymScalar root) {
    if (root.isScalar()) {
      Scalar scalar = (Scalar) root.tensor();
      int index = scalar.number().intValue();
      max = Math.max(max, index);
      while (sum.length() <= index)
        sum.append(RealScalar.ZERO);
      sum.set(value -> value.add(weight), index);
    } else {
      visit(weight.multiply(RealScalar.ONE.subtract(root.ratio())), root.getP());
      visit(weight.multiply(root.ratio()), root.getQ());
    }
  }

  Tensor vector() {
    return sum.extract(0, max + 1);
  }
}
