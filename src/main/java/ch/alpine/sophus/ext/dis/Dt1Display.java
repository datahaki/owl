// code by jph
package ch.alpine.sophus.ext.dis;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.LieTransport;
import ch.alpine.sophus.lie.dt.DtBiinvariantMean;
import ch.alpine.sophus.lie.dt.DtExponential;
import ch.alpine.sophus.lie.dt.DtGeodesic;
import ch.alpine.sophus.lie.dt.DtGroup;
import ch.alpine.sophus.lie.dt.DtManifold;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.red.Max;

public enum Dt1Display implements ManifoldDisplay {
  INSTANCE;

  private static final Tensor PENTAGON = CirclePoints.of(5).multiply(RealScalar.of(0.2)).unmodifiable();
  // Fehlerhaft, aber zurzeit Probleme mit Ausnahme bei lambda = 0
  private static final ScalarUnaryOperator MAX_X = Max.function(RealScalar.of(0.001));

  @Override // from GeodesicDisplay
  public Geodesic geodesic() {
    return DtGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 2;
  }

  @Override // from GeodesicDisplay
  public TensorUnaryOperator tangentProjection(Tensor p) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return PENTAGON;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor point = xya.extract(0, 2);
    point.set(MAX_X, 0);
    return point;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return VectorQ.requireLength(p, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return GfxMatrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return DtGroup.INSTANCE;
  }

  @Override
  public LieExponential lieExponential() {
    return LieExponential.of(DtGroup.INSTANCE, DtExponential.INSTANCE);
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return DtManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return LieTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public Biinvariant metricBiinvariant() {
    return null;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return DtBiinvariantMean.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from Object
  public String toString() {
    return "Dt1";
  }
}
