// code by jph
package ch.alpine.java.win;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ArrayPlot;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.mat.GaussianMatrix;

/* package */ enum IconDemo {
  ;
  public static void main(String[] args) throws Exception {
    for (ColorDataGradients colorDataFunction : ColorDataGradients.values()) {
      Tensor matrix = GaussianMatrix.of(11);
      matrix = matrix.map(scalar -> Scalars.lessThan(RealScalar.of(0.001), scalar) ? scalar : DoubleScalar.INDETERMINATE);
      Tensor image = ArrayPlot.of(matrix, colorDataFunction);
      System.out.println(Dimensions.of(image));
      Export.of(HomeDirectory.Pictures(colorDataFunction.name() + ".png"), image);
    }
  }
}
