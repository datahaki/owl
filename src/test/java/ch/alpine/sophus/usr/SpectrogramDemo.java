// code by jph
package ch.alpine.sophus.usr;

import java.io.File;
import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.fft.Spectrogram;
import ch.alpine.tensor.img.ArrayPlot;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.num.Series;
import ch.alpine.tensor.sca.Cos;
import ch.alpine.tensor.sca.win.DirichletWindow;

/** Example from Mathematica::Spectrogram:
 * Table[Cos[ i/4 + (i/20)^2], {i, 2000}] */
/* package */ enum SpectrogramDemo {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = Subdivide.of(0, 100, 2000).map(Series.of(Tensors.vector(0, 5, 1))).map(Cos.FUNCTION);
    Tensor spectrogram = Spectrogram.array(tensor, DirichletWindow.FUNCTION);
    File folder = HomeDirectory.Pictures(SpectrogramDemo.class.getSimpleName());
    folder.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor image = ArrayPlot.of(spectrogram, colorDataGradients);
      Export.of(new File(folder, colorDataGradients.name() + ".png"), ImageResize.nearest(image, 4));
    }
  }
}
