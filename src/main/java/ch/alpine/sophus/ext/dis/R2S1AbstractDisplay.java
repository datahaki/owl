// code by jph
package ch.alpine.sophus.ext.dis;

import java.io.Serializable;

import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.decim.LineDistance;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;

public abstract class R2S1AbstractDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return GfxMatrix.of(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return null;
  }

  @Override
  public LieExponential lieExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return null;
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return null;
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
    return null;
  }

  @Override
  public LineDistance lineDistance() {
    return null;
  }
}
