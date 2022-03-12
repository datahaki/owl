// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.Serializable;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.crv.d2.PolygonNormalize;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.ext.api.Spearhead;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;

// TODO OWL ALG probably obsolete: instead use Se2 and Se2Covering with different clothoid builders
public abstract class AbstractClothoidDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor SPEARHEAD = PolygonNormalize.of( //
      Spearhead.of(Tensors.vector(-0.217, -0.183, 4.189), RealScalar.of(0.1)), RealScalar.of(0.08));
  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override
  public abstract ClothoidBuilder geodesic();

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return SPEARHEAD;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor p) {
    return v -> v.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return GfxMatrix.of(p);
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final LieExponential lieExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsManifold hsManifold() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return (p, q) -> geodesic().curve(p, q).length();
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return null;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from Object
  public abstract String toString();
}
