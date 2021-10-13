// code by jph
package ch.alpine.sophus.demo.io;

import java.io.IOException;

import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.lie.se3.Se3Differences;
import ch.alpine.sophus.lie.se3.Se3Geodesic;
import ch.alpine.sophus.lie.se3.Se3Matrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Put;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.lie.QuaternionToRotationMatrix;
import ch.alpine.tensor.sca.win.WindowFunctions;

/** the quaternions in the data set have norm of approximately
 * 1.00005... due to the use of float precision */
/* package */ enum EurocData {
  ;
  private static Tensor rowmap(Tensor row) {
    Tensor p = row.extract(1, 4);
    Tensor R = QuaternionToRotationMatrix.of(Quaternion.of(row.Get(4), row.extract(5, 8)));
    return Se3Matrix.of(R, p);
    // return Tensors.of( //
    // Join.of(R.get(0), p.extract(0, 1)), //
    // Join.of(R.get(1), p.extract(1, 2)), //
    // Join.of(R.get(2), p.extract(2, 3)), //
    // Tensors.vector(0, 0, 0, 1));
  }

  public static void main(String[] args) throws IOException {
    Tensor tensor = ResourceData.of("/3rdparty/app/pose/euroc/MH_04_difficult.csv");
    System.out.println(Dimensions.of(tensor));
    Export.of(HomeDirectory.file("MH_04_difficult_time.csv"), tensor.get(Tensor.ALL, 0));
    Tensor poses = Tensor.of(tensor.stream().limit(12500).map(EurocData::rowmap));
    System.out.println(Dimensions.of(poses));
    Put.of(HomeDirectory.file("MH_04_difficult_poses.file"), poses);
    System.out.println("differences");
    {
      Tensor delta = Se3Differences.INSTANCE.apply(poses);
      Put.of(HomeDirectory.file("MH_04_difficult_delta.file"), delta);
    }
    System.out.println("smooth");
    {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(Se3Geodesic.INSTANCE, WindowFunctions.GAUSSIAN.get()), 4 * 3 * 2);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      System.out.println("store");
      Put.of(HomeDirectory.file("MH_04_difficult_poses_smooth.file"), smooth);
      System.out.println("differences");
      Tensor delta = Se3Differences.INSTANCE.apply(smooth);
      System.out.println("store");
      Put.of(HomeDirectory.file("MH_04_difficult_delta_smooth.file"), delta);
    }
  }
}
