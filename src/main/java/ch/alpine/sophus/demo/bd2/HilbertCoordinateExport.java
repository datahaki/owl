// code by jph
package ch.alpine.sophus.demo.bd2;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.alpine.sophus.ext.dis.R2Display;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;

/* package */ enum HilbertCoordinateExport {
  ;
  public static void main(String[] args) throws IOException {
    for (int n = 2; n < 5; ++n) {
      System.out.println(n);
      Tensor sequence = HilbertBenchmarkDemo.unit(n);
      BufferedImage bufferedImage = HilbertLevelImage.of( //
          R2Display.INSTANCE, sequence, 60, ColorDataGradients.CLASSIC, 800);
      ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures(String.format("hc%d.png", n)));
    }
  }
}
