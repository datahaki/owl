// code by jph
package ch.alpine.sophus.gui.ren;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Rescale;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.sca.Clip;

public class ArrayPlotRender implements RenderInterface {
  /** @param tensor
   * @param colorDataGradient
   * @param magnify
   * @return */
  public static ArrayPlotRender rescale(Tensor tensor, ScalarTensorFunction colorDataGradient, int magnify) {
    Rescale rescale = new Rescale(tensor);
    return new ArrayPlotRender( //
        rescale.result(), //
        rescale.scalarSummaryStatistics().getClip(), //
        colorDataGradient, //
        magnify);
  }

  // ---
  private final BufferedImage bufferedImage;
  private final int width;
  private final int height;
  private final BufferedImage legend;

  public ArrayPlotRender(Tensor tensor, Clip clip, ScalarTensorFunction colorDataGradient, int magnify) {
    bufferedImage = ImageFormat.of(tensor.map(colorDataGradient));
    width = bufferedImage.getWidth() * magnify;
    height = bufferedImage.getHeight() * magnify;
    legend = LegendImage.of(colorDataGradient, height, clip);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, //
        0, //
        0, //
        width, //
        height, null);
    graphics.drawImage(legend, //
        width + 10, //
        0, //
        null);
  }

  public int height() {
    return height;
  }

  public BufferedImage export() {
    BufferedImage bi = new BufferedImage( //
        width + 10 + legend.getWidth(), // magic constant corresponds to width of legend
        height, //
        BufferedImage.TYPE_INT_ARGB);
    render(null, bi.createGraphics());
    return bi;
  }

  public Dimension getDimension() {
    return new Dimension(width, height);
  }
}
