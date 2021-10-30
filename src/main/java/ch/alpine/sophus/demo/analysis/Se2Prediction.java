// code by jph
package ch.alpine.sophus.demo.analysis;

import java.io.IOException;

import ch.alpine.java.fig.ArrayPlot;
import ch.alpine.sophus.bm.BiinvariantMeans;
import ch.alpine.sophus.demo.io.GokartPoseData;
import ch.alpine.sophus.demo.io.GokartPoseDataV2;
import ch.alpine.sophus.lie.se2.Se2BiinvariantMeans;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Partition;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Put;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Abs;

/* package */ enum Se2Prediction {
  ;
  public static void main(String[] args) throws IOException {
    GokartPoseData gokartPoseData = GokartPoseDataV2.INSTANCE;
    String name = gokartPoseData.list().get(1);
    Tensor pqr_t = Partition.of(gokartPoseData.getPose(name, 4 * 500), 4);
    Tensor Wp = Subdivide.of(-1.5, 0.0, 25);
    Tensor Wq = Subdivide.of(-1.0, 1.0, 25);
    Tensor err_xy = Array.zeros(Wp.length(), Wq.length());
    Tensor err_hd = Array.zeros(Wp.length(), Wq.length());
    Tensor lp3_xy = Array.zeros(Wp.length(), Wq.length(), 3);
    Tensor lp3_hd = Array.zeros(Wp.length(), Wq.length(), 3);
    // Tensor rotation = RotationMatrix.of(0);
    int i = 0;
    for (Tensor wp : Wp) {
      int j = 0;
      for (Tensor wq : Wq) {
        // Tensor wpq = Tensors.of(wp.subtract(wq), wp.add(wq));
        Tensor wpq = Tensors.of(wp, wq);
        Scalar wr = RealScalar.ONE.subtract(Total.ofVector(wpq));
        Tensor weights = wpq.append(wr);
        TensorUnaryOperator tensorUnaryOperator = BiinvariantMeans.of(Se2BiinvariantMeans.FILTER, weights);
        for (Tensor sequence : pqr_t) {
          Tensor pqr = sequence.extract(0, 3);
          Tensor t_prediction = tensorUnaryOperator.apply(pqr);
          Tensor t_measured = sequence.get(3);
          {
            Scalar err = Vector2Norm.between(t_prediction.extract(0, 2), t_measured.extract(0, 2));
            err_xy.set(err::add, i, j);
          }
          {
            Scalar diff = So2.MOD.apply(Abs.between(t_prediction.Get(2), t_measured.Get(2)));
            err_hd.set(diff::add, i, j);
          }
        }
        lp3_xy.set(wp, i, j, 0);
        lp3_xy.set(wq, i, j, 1);
        lp3_hd.set(wp, i, j, 0);
        lp3_hd.set(wq, i, j, 1);
        ++j;
      }
      ++i;
    }
    lp3_xy.set(err_xy, Tensor.ALL, Tensor.ALL, 2);
    lp3_hd.set(err_hd, Tensor.ALL, Tensor.ALL, 2);
    {
      Put.of(HomeDirectory.file("lp3_xy.mathematica"), lp3_xy);
      Put.of(HomeDirectory.file("lp3_hd.mathematica"), lp3_hd);
    }
    {
      Tensor image = Raster.of(err_xy, ColorDataGradients.CLASSIC);
      Export.of(HomeDirectory.file("err_xy.csv"), err_xy);
      Export.of(HomeDirectory.Pictures(Se2Prediction.class.getSimpleName() + "_xy.png"), image);
    }
    {
      Tensor image = Raster.of(err_hd, ColorDataGradients.CLASSIC);
      Export.of(HomeDirectory.file("err_hd.csv"), err_hd);
      Export.of(HomeDirectory.Pictures(Se2Prediction.class.getSimpleName() + "_hd.png"), image);
    }
  }
}
