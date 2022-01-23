// code by jph
package ch.alpine.java.ren;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Round;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/BarLegend.html">BarLegend</a> */
public enum BarLegend {
  ;
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
  private static final Color FONT_COLOR = Color.BLACK;

  public static BufferedImage of(ScalarTensorFunction colorDataGradient, int height, Clip clip) {
    String smax = Objects.nonNull(clip) ? clip.max().map(Round._3).toString() : "";
    String smin = Objects.nonNull(clip) ? clip.min().map(Round._3).toString() : "";
    return _of(colorDataGradient, height, smax, smin);
  }

  public static BufferedImage of(ScalarTensorFunction colorDataGradient, int height, String smax, String smin) {
    return _of(colorDataGradient, height, smax, smin);
  }

  // ---
  private static int fontw(String smax, String smin) {
    Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
    graphics.setFont(FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    return Math.max( //
        fontMetrics.stringWidth(smin), //
        fontMetrics.stringWidth(smax));
  }

  private static BufferedImage _of(ScalarTensorFunction colorDataGradient, int height, String smax, String smin) {
    int width = 10;
    int space = 2;
    BufferedImage bufferedImage = new BufferedImage(width + space + fontw(smax, smin), height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.drawImage( //
        ImageFormat.of(Subdivide.decreasing(Clips.unit(), height - 1).map(Tensors::of).map(colorDataGradient)), //
        0, //
        0, //
        width, //
        height, null);
    graphics.setFont(FONT);
    graphics.setColor(FONT_COLOR);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int wmax = fontMetrics.stringWidth(smax);
    int wmin = fontMetrics.stringWidth(smin);
    int ofx = width + space + Math.max(wmin, wmax);
    RenderQuality.setQuality(graphics);
    graphics.drawString(smax, ofx - wmax, fontMetrics.getAscent());
    graphics.drawString(smin, ofx - wmin, height);
    RenderQuality.setDefault(graphics);
    return bufferedImage;
  }
}
