// code by jph
package ch.alpine.sophus.ext.api;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.alpine.sophus.api.Genesis;
import ch.alpine.sophus.gbc.AffineCoordinate;
import ch.alpine.sophus.gbc.LagrangeCoordinate;
import ch.alpine.sophus.gbc.LeveragesGenesis;
import ch.alpine.sophus.gbc.MetricCoordinate;
import ch.alpine.sophus.gbc.amp.Amplifiers;
import ch.alpine.sophus.gbc.d2.Barycenter;
import ch.alpine.sophus.gbc.d2.InsideConvexHullCoordinate;
import ch.alpine.sophus.gbc.d2.InsidePolygonCoordinate;
import ch.alpine.sophus.gbc.d2.IterativeCoordinate;
import ch.alpine.sophus.gbc.d2.IterativeMeanValueCoordinate;
import ch.alpine.sophus.gbc.d2.ThreePointCoordinate;
import ch.alpine.sophus.gbc.it.IterativeAffineCoordinate;
import ch.alpine.sophus.gbc.it.IterativeTargetCoordinate;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsGenesis;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.sophus.itp.InverseDistanceWeighting;
import ch.alpine.sophus.math.var.InversePowerVariogram;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;

public enum PolygonCoordinates implements LogWeighting {
  MEAN_VALUE(ThreePointCoordinate.of(Barycenter.MEAN_VALUE)), //
  // CIRCULAR(CircularCoordinate.INSTANCE), //
  ITERATIVE_MV_1(IterativeMeanValueCoordinate.of(1)), //
  ITERATIVE_MV_2(IterativeMeanValueCoordinate.of(2)), //
  ITERATIVE_MV_3(IterativeMeanValueCoordinate.of(3)), //
  ITERATIVE_MV_5(IterativeMeanValueCoordinate.of(5)), //
  WACHSPRESS(ThreePointCoordinate.of(Barycenter.WACHSPRESS)), //
  DISCRETE_HARMONIC(ThreePointCoordinate.of(Barycenter.DISCRETE_HARMONIC)), //
  INVERSE_DISTANCE(MetricCoordinate.of(InversePowerVariogram.of(2))), //
  LAGRANG_DISTANCE(new LagrangeCoordinate(new InverseDistanceWeighting(InversePowerVariogram.of(2)))), //
  ITER_TARGET(new IterativeTargetCoordinate(new InverseDistanceWeighting(InversePowerVariogram.of(2)), RealScalar.ONE, 50)), //
  ITERATIVE_AF_0(new IterativeCoordinate(AffineCoordinate.INSTANCE, 0)), //
  ITERATIVE_AF_1(new IterativeCoordinate(AffineCoordinate.INSTANCE, 1)), //
  ITERATIVE_AF_2(new IterativeCoordinate(AffineCoordinate.INSTANCE, 2)), //
  ITERATIVE_AF_3(new IterativeCoordinate(AffineCoordinate.INSTANCE, 3)), //
  ITERATIVE_AF_5(new IterativeCoordinate(AffineCoordinate.INSTANCE, 5)), //
  ITERATIVE_EX_05(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 05)), //
  ITERATIVE_EX_10(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 10)), //
  ITERATIVE_EX_20(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 20)), //
  ITERATIVE_EX_30(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 30)), //
  ITERATIVE_EX_50(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 50)), //
  TARGET(new LeveragesGenesis(InversePowerVariogram.of(2))), //
  ITERATIVE_IL_0(new IterativeCoordinate(new LeveragesGenesis(InversePowerVariogram.of(2)), 0)), //
  ITERATIVE_IL_1(new IterativeCoordinate(new LeveragesGenesis(InversePowerVariogram.of(2)), 1)), //
  ITERATIVE_IL_2(new IterativeCoordinate(new LeveragesGenesis(InversePowerVariogram.of(2)), 2)), //
  ITERATIVE_IL_3(new IterativeCoordinate(new LeveragesGenesis(InversePowerVariogram.of(2)), 3)), //
  ITERATIVE_IL_5(new IterativeCoordinate(new LeveragesGenesis(InversePowerVariogram.of(2)), 5)), //
  ;

  private static final Set<PolygonCoordinates> CONVEX = //
      EnumSet.of(INVERSE_DISTANCE, LAGRANG_DISTANCE, ITER_TARGET, TARGET);
  // ---
  private final Genesis genesis;

  private PolygonCoordinates(Genesis genesis) {
    this.genesis = genesis;
  }

  @Override // from LogWeighting
  public TensorUnaryOperator operator( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence) {
    return HsGenesis.wrap( //
        vectorLogManifold, //
        CONVEX.contains(this) //
            ? new InsideConvexHullCoordinate(genesis)
            : new InsidePolygonCoordinate(genesis), //
        sequence);
  }

  @Override // from LogWeighting
  public TensorScalarFunction function( //
      Biinvariant biinvariant, // <- ignored
      VectorLogManifold vectorLogManifold, // with 2 dimensional tangent space
      ScalarUnaryOperator variogram, // <- ignored
      Tensor sequence, Tensor values) {
    TensorUnaryOperator tensorUnaryOperator = operator(biinvariant, vectorLogManifold, variogram, sequence);
    Objects.requireNonNull(values);
    return point -> (Scalar) tensorUnaryOperator.apply(point).dot(values);
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }

  public Genesis genesis() {
    return genesis;
  }
}
