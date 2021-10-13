// code by ob, jph
package ch.alpine.sophus.demo.filter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.alpine.sophus.demo.io.GokartPoseData;
import ch.alpine.sophus.demo.io.GokartPoseDataV1;
import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.lie.se2.Se2Differences;
import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.sca.win.WindowFunctions;

/* package */ class SpectrogramDataExport {
  private final GokartPoseData gokartPoseData;

  public SpectrogramDataExport(GokartPoseData gokartPoseData) {
    this.gokartPoseData = gokartPoseData;
  }

  private void process(File ROOT) throws IOException {
    List<String> dataSource = gokartPoseData.list();
    List<WindowFunctions> kernel = Arrays.asList(WindowFunctions.GAUSSIAN, WindowFunctions.HAMMING, WindowFunctions.BLACKMAN);
    // iterate over data
    for (String data : dataSource) {
      // iterate over Kernels
      // load data
      Tensor control = gokartPoseData.getPose(data, Integer.MAX_VALUE);
      for (WindowFunctions windowFunctions : kernel) {
        ScalarUnaryOperator smoothingKernel = windowFunctions.get();
        // iterate over radius
        // Create Geod. Center instance
        TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(Se2Geodesic.INSTANCE, smoothingKernel);
        for (int radius = 0; radius < 15; radius++) {
          // Create new Geod. Center
          Tensor refined = CenterFilter.of(tensorUnaryOperator, radius).apply(control);
          System.out.println(data + smoothingKernel + radius);
          System.err.println(speeds(refined));
          // export velocities
          Export.of(new File(ROOT, "190319/" + data.replace('/', '_') + "_" + smoothingKernel + "_" + radius + ".csv"), refined);
        }
      }
    }
  }

  private Tensor speeds(Tensor refined) {
    return Se2Differences.INSTANCE.apply(refined).multiply(gokartPoseData.getSampleRate());
  }

  public static void main(String[] args) throws IOException {
    SpectrogramDataExport spectrogramDataExport = new SpectrogramDataExport(GokartPoseDataV1.INSTANCE);
    spectrogramDataExport.process(HomeDirectory.Desktop("MA/owl_export"));
  }
}