// code by jph
package ch.alpine.owl.math.noise;

import java.io.IOException;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Round;

/* package */ enum ColoredNoiseExport {
  ;
  public static void main(String[] args) throws IOException {
    for (Tensor _x : Subdivide.of(0, 2, 10)) {
      ColoredNoise coloredNoise = new ColoredNoise(((Scalar) _x).number().doubleValue());
      Tensor tensor = RandomVariate.of(coloredNoise, 10000);
      Export.of(HomeDirectory.file("cn" + _x.map(Round._1) + ".csv.gz"), tensor);
    }
  }
}
