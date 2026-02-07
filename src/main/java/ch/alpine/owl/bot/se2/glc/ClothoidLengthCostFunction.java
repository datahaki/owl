// code by gjoel
package ch.alpine.owl.bot.se2.glc;

import java.util.Objects;
import java.util.function.Predicate;

import ch.alpine.sophis.crv.clt.Clothoid;
import ch.alpine.sophis.crv.clt.ClothoidBuilder;
import ch.alpine.sophis.crv.clt.ClothoidBuilders;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;

/** only applied in {@link ClothoidPursuitControl} resp. {@link Se2Letter5Demo} */
/* package */ class ClothoidLengthCostFunction implements TensorScalarFunction {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  // ---
  private final Predicate<Scalar> isCompliant;

  public ClothoidLengthCostFunction(Predicate<Scalar> isCompliant) {
    this.isCompliant = Objects.requireNonNull(isCompliant);
  }

  @Override
  public Scalar apply(Tensor xya) {
    Clothoid clothoid = CLOTHOID_BUILDER.curve(xya.map(Scalar::zero), xya);
    if (isCompliant.test(clothoid.curvature().maxAbs()))
      return clothoid.length();
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(xya.Get(0)));
  }
}
