// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.sophus.gbc.AffineCoordinate;
import ch.ethz.idsc.sophus.gbc.Amplifiers;
import ch.ethz.idsc.sophus.gbc.Genesis;
import ch.ethz.idsc.sophus.gbc.HsCoordinates;
import ch.ethz.idsc.sophus.gbc.IterativeAffineCoordinate;
import ch.ethz.idsc.sophus.gbc.IterativeTargetCoordinate;
import ch.ethz.idsc.sophus.gbc.MetricCoordinate;
import ch.ethz.idsc.sophus.gbc.TargetCoordinate;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.krg.InverseDistanceWeighting;
import ch.ethz.idsc.sophus.lie.r2.Barycenter;
import ch.ethz.idsc.sophus.lie.r2.InsideConvexHullCoordinate;
import ch.ethz.idsc.sophus.lie.r2.InsidePolygonCoordinate;
import ch.ethz.idsc.sophus.lie.r2.IterativeCoordinate;
import ch.ethz.idsc.sophus.lie.r2.IterativeMeanValueCoordinate;
import ch.ethz.idsc.sophus.lie.r2.ThreePointCoordinate;
import ch.ethz.idsc.sophus.math.var.InversePowerVariogram;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

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
  ITER_TARGET(new IterativeTargetCoordinate(InverseDistanceWeighting.of(InversePowerVariogram.of(2)), 50)), //
  ITERATIVE_AF_0(IterativeCoordinate.of(AffineCoordinate.INSTANCE, 0)), //
  ITERATIVE_AF_1(IterativeCoordinate.of(AffineCoordinate.INSTANCE, 1)), //
  ITERATIVE_AF_2(IterativeCoordinate.of(AffineCoordinate.INSTANCE, 2)), //
  ITERATIVE_AF_3(IterativeCoordinate.of(AffineCoordinate.INSTANCE, 3)), //
  ITERATIVE_AF_5(IterativeCoordinate.of(AffineCoordinate.INSTANCE, 5)), //
  ITERATIVE_EX_05(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 05)), //
  ITERATIVE_EX_10(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 10)), //
  ITERATIVE_EX_20(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 20)), //
  ITERATIVE_EX_30(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 30)), //
  ITERATIVE_EX_50(new IterativeAffineCoordinate(Amplifiers.EXP.supply(5), 50)), //
  TARGET(TargetCoordinate.of(InversePowerVariogram.of(2))), //
  ITERATIVE_IL_0(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 0)), //
  ITERATIVE_IL_1(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 1)), //
  ITERATIVE_IL_2(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 2)), //
  ITERATIVE_IL_3(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 3)), //
  ITERATIVE_IL_5(IterativeCoordinate.of(TargetCoordinate.of(InversePowerVariogram.of(2)), 5)), //
  ;

  private static final Set<PolygonCoordinates> CONVEX = EnumSet.of(INVERSE_DISTANCE, ITER_TARGET, TARGET);
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
    return WeightingOperators.wrap( //
        HsCoordinates.wrap(vectorLogManifold, CONVEX.contains(this) //
            ? InsideConvexHullCoordinate.of(genesis)
            : InsidePolygonCoordinate.of(genesis)), //
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
    return point -> tensorUnaryOperator.apply(point).dot(values).Get();
  }

  public static List<LogWeighting> list() {
    return Arrays.asList(values());
  }

  public Genesis genesis() {
    return genesis;
  }
}
