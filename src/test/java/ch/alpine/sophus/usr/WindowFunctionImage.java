// code by jph
package ch.alpine.sophus.usr;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.sca.win.WindowFunctions;

// 3
/* package */ enum WindowFunctionImage {
  ;
  private static Tensor image() {
    Tensor SE2 = Tensors.fromString("{{180, 0, 6+180/2}, {0, -180, 186}, {0, 0, 1}}").unmodifiable();
    GeometricLayer geometricLayer = GeometricLayer.of(SE2);
    BufferedImage bufferedImage = StaticHelper.createWhite();
    Graphics2D graphics = bufferedImage.createGraphics();
    RenderQuality.setQuality(graphics);
    graphics.setColor(Color.RED);
    Tensor domain = Subdivide.of(-0.5, 0.5, 180);
    graphics.setStroke(new BasicStroke(1.5f));
    ColorDataIndexed colorDataIndexedF = ColorDataLists._097.cyclic();
    ColorDataIndexed colorDataIndexedP = colorDataIndexedF.deriveWithAlpha(128 + 64);
    int piy = 10;
    int index = 0;
    WindowFunctions[] smoothingKernels = new WindowFunctions[] { //
        WindowFunctions.GAUSSIAN, //
        WindowFunctions.HAMMING, //
        WindowFunctions.BLACKMAN, //
        WindowFunctions.NUTTALL, //
    };
    graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
    for (WindowFunctions smoothingKernel : smoothingKernels) {
      graphics.setColor(colorDataIndexedP.getColor(index));
      Tensor tensor = domain.map(smoothingKernel.get());
      Tensor points = Transpose.of(Tensors.of(domain, tensor));
      Path2D path2d = geometricLayer.toPath2D(points);
      graphics.draw(path2d);
      graphics.setColor(colorDataIndexedF.getColor(index));
      graphics.drawString(smoothingKernel.name(), 0, piy);
      piy += 10;
      ++index;
    }
    return ImageFormat.from(bufferedImage);
  }

  public static void main(String[] args) throws IOException {
    Export.of(HomeDirectory.Pictures(WindowFunctionImage.class.getSimpleName() + ".png"), image());
  }
}
