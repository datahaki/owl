// code by jph
package ch.alpine.sophus.opt;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ch.alpine.sophus.app.io.GokartPoseData;
import ch.alpine.sophus.app.io.GokartPoseDataV1;
import ch.alpine.sophus.app.io.GokartPoseDataV2;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class GeodesicFiltersTest extends TestCase {
  private static void _check(GokartPoseData gokartPoseData) {
    List<String> lines = gokartPoseData.list();
    Tensor control = gokartPoseData.getPose(lines.get(0), 250);
    ManifoldDisplay geodesicDisplay = Se2Display.INSTANCE;
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    ScalarUnaryOperator smoothingKernel = WindowFunctions.GAUSSIAN.get();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    int radius = 7;
    Map<GeodesicFilters, Tensor> map = new EnumMap<>(GeodesicFilters.class);
    for (GeodesicFilters geodesicFilters : GeodesicFilters.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          geodesicFilters.supply(geodesicInterface, smoothingKernel, biinvariantMean);
      Tensor filtered = CenterFilter.of(tensorUnaryOperator, radius).apply(control);
      map.put(geodesicFilters, filtered);
    }
    for (GeodesicFilters lieGroupFilters : GeodesicFilters.values()) {
      Tensor diff = map.get(lieGroupFilters).subtract(map.get(GeodesicFilters.BIINVARIANT_MEAN));
      diff.set(So2.MOD, Tensor.ALL, 2);
      Scalar norm = MatrixInfinityNorm.of(diff);
      Chop._02.requireZero(norm);
    }
  }

  public void testSimple() {
    _check(GokartPoseDataV1.INSTANCE);
    _check(GokartPoseDataV2.INSTANCE);
  }

  public void testTiming() {
    String name = "20190701T170957_06";
    Tensor control = GokartPoseDataV2.RACING_DAY.getPose(name, 1_000_000);
    ManifoldDisplay geodesicDisplay = Se2Display.INSTANCE;
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    ScalarUnaryOperator smoothingKernel = WindowFunctions.GAUSSIAN.get();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    for (int radius : new int[] { 0, 10 }) {
      for (GeodesicFilters geodesicFilters : GeodesicFilters.values()) {
        TensorUnaryOperator tensorUnaryOperator = //
            geodesicFilters.supply(geodesicInterface, smoothingKernel,
                // lieGroup, exponential,
                biinvariantMean);
        Timing timing = Timing.started();
        CenterFilter.of(tensorUnaryOperator, radius).apply(control);
        timing.stop();
        // System.out.println(lieGroupFilters+" "+timing.seconds());
      }
    }
  }
}
