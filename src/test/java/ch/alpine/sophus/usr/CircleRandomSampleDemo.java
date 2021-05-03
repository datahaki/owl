// code by jph
package ch.alpine.sophus.usr;

import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Put;

/** demo exports random samples from a circle that for visualization in Mathematica:
 * A = << "samples.txt"; ListPlot[A, AspectRatio -> 1] */
/* package */ enum CircleRandomSampleDemo {
  ;
  public static void main(String[] args) throws Exception {
    RandomSampleInterface randomSampleInterface = //
        BallRandomSample.of(Tensors.vector(1, 1), RealScalar.of(2));
    Tensor matrix = RandomSample.of(randomSampleInterface, 10000);
    Put.of(HomeDirectory.file("samples.txt"), matrix);
  }
}
