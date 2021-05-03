// code by jph
package ch.alpine.sophus.app.se3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ch.alpine.sophus.app.io.UzhSe3TxtFormat;
import ch.alpine.sophus.decim.CurveDecimation;
import ch.alpine.sophus.decim.CurveDecimation.Result;
import ch.alpine.sophus.lie.se3.Se3CurveDecimation;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.io.Put;

/** the quaternions in the data set have norm of approximately
 * 1.00005... due to the use of float precision */
/* package */ enum UzhSe3Decimation {
  ;
  static void of(String name) throws FileNotFoundException, IOException {
    System.out.println(name);
    File root = HomeDirectory.Documents("uzh", name);
    root.mkdirs();
    // ---
    File file = new File("/media/datahaki/media/resource/uzh/groundtruth", name + ".txt");
    Tensor poses = UzhSe3TxtFormat.of(file);
    System.out.println(Dimensions.of(poses));
    Put.of(new File(root, "poses.file"), poses);
    {
      CurveDecimation curveDecimation = Se3CurveDecimation.of(RealScalar.of(0.02));
      Timing timing = Timing.started();
      Result result = curveDecimation.evaluate(poses);
      Tensor decimated = result.result();
      timing.stop();
      System.out.println(timing.seconds());
      System.out.println(Dimensions.of(decimated));
      Put.of(new File(root, "decimated.file"), decimated);
      Put.of(new File(root, "error.file"), result.errors());
    }
  }

  public static void main(String[] args) throws IOException {
    for (UzhSe3Data uzhSe3Data : UzhSe3Data.values()) {
      of(uzhSe3Data.name());
    }
  }
}
