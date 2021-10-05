// code by jph
package ch.alpine.sophus.app.io;

import java.io.IOException;

import ch.alpine.sophus.flt.CenterFilter;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.lie.se2.Se2Differences;
import ch.alpine.sophus.lie.se2.Se2Geodesic;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Put;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.sca.win.WindowFunctions;

/* package */ enum EugocData {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = ResourceData.of("/dubilab/app/pose/2r/20180820T165637_2.csv");
    // System.out.println(Dimensions.of(tensor));
    Tensor poses = Tensor.of(tensor.stream() //
        .map(row -> row.extract(1, 4)));
    // Tensors.empty();
    // for (Tensor row : tensor) {
    // Tensor xyt = row.extract(1, 4);
    // poses.append(xyt);
    // }
    System.out.println(Dimensions.of(poses));
    Put.of(HomeDirectory.file("gokart_poses.file"), poses);
    {
      Tensor delta = Se2Differences.INSTANCE.apply(poses);
      Put.of(HomeDirectory.file("gokart_delta.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, WindowFunctions.GAUSSIAN.get()), 6);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(HomeDirectory.file("gokart_poses_gauss.file"), smooth);
      Tensor delta = Se2Differences.INSTANCE.apply(smooth);
      Put.of(HomeDirectory.file("gokart_delta_gauss.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, WindowFunctions.HAMMING.get()), 6);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(HomeDirectory.file("gokart_poses_hammi.file"), smooth);
      Tensor delta = Se2Differences.INSTANCE.apply(smooth);
      Put.of(HomeDirectory.file("gokart_delta_hammi.file"), delta);
    }
  }
}
