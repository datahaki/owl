// code by jph
package ch.alpine.owl.math.noise;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.fft.Spectrogram;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.win.DirichletWindow;

/* package */ enum ColoredNoiseSpectrogram {
  ;
  public static void main(String[] args) throws IOException {
    ColoredNoise coloredNoise = new ColoredNoise(1); // 1 == pink noise
    Tensor tensor = RandomVariate.of(coloredNoise, 1024 * 4);
    Tensor image = Spectrogram.of(tensor, DirichletWindow.FUNCTION, ColorDataGradients.VISIBLESPECTRUM);
    Export.of(HomeDirectory.Pictures(ColoredNoiseSpectrogram.class.getSimpleName() + ".png"), image);
  }
}
