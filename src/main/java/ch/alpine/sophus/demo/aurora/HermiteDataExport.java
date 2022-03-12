// code by jph
package ch.alpine.sophus.demo.aurora;

import java.io.File;
import java.io.IOException;

import ch.alpine.sophus.api.TensorIteration;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.crv.d2.Curvature2D;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.ext.api.HermiteSubdivisions;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.hs.HsTransport;
import ch.alpine.sophus.lie.LieTransport;
import ch.alpine.sophus.lie.rn.RnGeodesic;
import ch.alpine.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.alpine.sophus.lie.se2c.Se2CoveringManifold;
import ch.alpine.sophus.lie.so2.So2Lift;
import ch.alpine.sophus.math.Do;
import ch.alpine.sophus.ref.d1.BSpline1CurveSubdivision;
import ch.alpine.sophus.ref.d1.BSpline2CurveSubdivision;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.sophus.ref.d1h.HermiteSubdivision;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.red.Nest;

/* package */ class HermiteDataExport {
  private final int levels;
  private final File folder;
  private final Tensor control = Tensors.empty();
  private final Scalar delta;
  private final Tensor domain;

  /** @param name "20190701T163225_01"
   * @param period 1/2[s]
   * @param levels 4
   * @throws IOException */
  public HermiteDataExport(String name, Scalar period, int levels) throws IOException {
    this.levels = Integers.requirePositive(levels);
    folder = HomeDirectory.Documents(name);
    folder.mkdir();
    Tensor data = GokartPoseDataV2.INSTANCE.getPoseVel(name, 2_000);
    data.set(new So2Lift(), Tensor.ALL, 0, 2);
    {
      Export.of(new File(folder, "gndtrth.mathematica"), data);
      Tensor domain1 = Range.of(0, data.length()).multiply(RealScalar.of(1 / 50.));
      Export.of(new File(folder, "gndtrth_domain.mathematica"), domain1);
    }
    Scalar rate = GokartPoseDataV2.INSTANCE.getSampleRate();
    delta = QuantityMagnitude.SI().in("s").apply(period);
    int skip = Scalars.intValueExact(period.multiply(rate));
    for (int index = 0; index < data.length(); index += skip)
      control.append(data.get(index));
    Export.of(new File(folder, "control.mathematica"), control);
    domain = Range.of(0, control.length()).multiply(delta);
    Export.of(new File(folder, "control_domain.mathematica"), domain);
  }

  private void process(HermiteSubdivision hermiteSubdivision, CurveSubdivision curveSubdivision, String name) throws IOException {
    TensorIteration tensorIteration = //
        hermiteSubdivision.string(delta, control);
    File dst = new File(folder, name);
    dst.mkdir();
    {
      Tensor refined = Do.of(tensorIteration::iterate, levels);
      Export.of(new File(dst, "refined.mathematica"), refined);
      Tensor curvatu = Curvature2D.string(Tensor.of(refined.stream().map(point -> point.get(0).extract(0, 2))));
      Export.of(new File(dst, "curvatu.mathematica"), curvatu);
    }
    {
      Tensor tensor = Nest.of(curveSubdivision::string, domain, levels);
      Export.of(new File(dst, "refined_domain.mathematica"), tensor);
    }
  }

  private void processAll() throws IOException {
    HsManifold hsManifold = Se2CoveringManifold.INSTANCE;
    HsTransport hsTransport = LieTransport.INSTANCE;
    BiinvariantMean biinvariantMean = Se2CoveringBiinvariantMean.INSTANCE;
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H1STANDARD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h1standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H2STANDARD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h2standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H2MANIFOLD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h2manifold");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3STANDARD.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3standard");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3A1.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3a1");
    }
    {
      HermiteSubdivision hermiteSubdivision = //
          HermiteSubdivisions.H3A2.supply(hsManifold, hsTransport, biinvariantMean);
      CurveSubdivision curveSubdivision = new BSpline1CurveSubdivision(RnGeodesic.INSTANCE);
      process(hermiteSubdivision, curveSubdivision, "h3a2");
    }
  }

  public static void main(String[] args) throws IOException {
    Scalar period = Quantity.of(RationalScalar.of(1, 1), "s");
    HermiteDataExport hermiteDataExport = new HermiteDataExport("20190701T163225_01", period, 6);
    hermiteDataExport.processAll();
  }
}
