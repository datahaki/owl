// code by gjoel, jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.AbstractTransition;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.LagrangeQuadraticD;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Drop;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.EqualizingDistribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;

public class ClothoidTransition extends AbstractTransition {
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final int MAX_INTERVALS = 511;

  /** @param clothoidBuilder
   * @param start of the form {px, py, p_angle}
   * @param end of the form {qx, qy, q_angle}
   * @return */
  public static ClothoidTransition of(ClothoidBuilder clothoidBuilder, Tensor start, Tensor end) {
    Clothoid clothoid = clothoidBuilder.curve(start, end);
    return new ClothoidTransition(start, end, clothoid);
  }

  /** @param start of the form {px, py, p_angle}
   * @param end of the form {qx, qy, q_angle}
   * @param clothoid
   * @return */
  public static ClothoidTransition of(Tensor start, Tensor end, Clothoid clothoid) {
    return new ClothoidTransition(start, end, clothoid);
  }

  public static ClothoidTransition of(Clothoid clothoid) {
    return of(clothoid.apply(_0), clothoid.apply(_1), clothoid);
  }

  // ---
  private final Clothoid clothoid;

  private ClothoidTransition(Tensor start, Tensor end, Clothoid clothoid) {
    super(start, end, clothoid.length());
    this.clothoid = clothoid;
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    Tensor uniform = Subdivide.of(_0, _1, //
        Math.max(1, Ceiling.intValueExact(length().divide(minResolution))));
    return Tensor.of(uniform.stream().skip(1).map(Scalar.class::cast).map(clothoid));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    int steps = Ceiling.intValueExact(length().divide(minResolution));
    Tensor samples = linearized(length().divide(RealScalar.of(steps)));
    return new TransitionWrap( //
        Drop.head(samples, 1), //
        ConstantArray.of(length().divide(RealScalar.of(samples.length())), samples.length() - 1));
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
    if (lagrangeQuadraticD.isZero(Tolerance.CHOP))
      return Tensors.of(clothoid.apply(_0), clothoid.apply(_1));
    // TODO check if units make sense
    Scalar multiply = Sqrt.FUNCTION.apply(lagrangeQuadraticD.integralAbs()).multiply(clothoid.length());
    int intervals = Ceiling.intValueExact(multiply.divide(minResolution));
    Tensor uniform = Subdivide.of(_0, _1, Math.min(Math.max(1, intervals), MAX_INTERVALS));
    InverseCDF inverseCDF = (InverseCDF) EqualizingDistribution.fromUnscaledPDF( //
        uniform.map(lagrangeQuadraticD).map(Abs.FUNCTION).map(Sqrt.FUNCTION));
    Tensor inverse = uniform.map(inverseCDF::quantile).divide(DoubleScalar.of(uniform.length()));
    return inverse.map(clothoid);
  }

  public Clothoid clothoid() {
    return clothoid;
  }
}
