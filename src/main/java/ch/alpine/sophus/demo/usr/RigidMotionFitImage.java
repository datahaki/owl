// code by jph
package ch.alpine.sophus.demo.usr;

import java.io.IOException;

import ch.alpine.sophus.fit.RigidMotionFit;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Pretty;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.ArcTan;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ class RigidMotionFitImage {
  private static Tensor shufflePoints(int n) {
    Distribution distribution = NormalDistribution.standard();
    Tensor random = RandomVariate.of(distribution, n, 2);
    Tensor mean = Mean.of(random).negate();
    return Tensor.of(random.stream().map(mean::add));
  }

  public static void main(String[] args) throws IOException {
    Tensor target = Array.zeros(1, 2);
    Tensor shuffl = shufflePoints(2);
    shuffl.stream().forEach(target::append);
    System.out.println(Pretty.of(target));
    Tensor points = target.copy();
    int RES = 128;
    Tensor param = Subdivide.of(-10, 10, RES);
    Clip clip = Clips.absolute(Pi.VALUE);
    Scalar[][] array = new Scalar[RES][RES];
    for (int x = 0; x < RES; ++x)
      for (int y = 0; y < RES; ++y) {
        points.set(Tensors.of(param.get(x), param.get(y)), 0);
        RigidMotionFit rigidMotionFit = RigidMotionFit.of(target, points);
        Tensor rotation = rigidMotionFit.rotation(); // 2 x 2
        Scalar angle = ArcTan.of(rotation.Get(0, 0), rotation.Get(1, 0));
        array[x][y] = clip.rescale(angle);
      }
    Tensor image = Raster.of(Tensors.matrix(array), ColorDataGradients.HUE);
    Export.of(HomeDirectory.Pictures("some.png"), image);
  }
}
