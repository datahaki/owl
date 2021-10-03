// code by jph
package ch.alpine.owl.gui.region;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.java.gfx.AffineTransforms;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.sca.N;

/** Hint:
 * On ubuntu, we have observed for grayscale images that the initial rendering
 * configuration influences the rendering when rotating the image. */
public class ImageRender implements RenderInterface {
  private static final Color COLOR = new Color(0, 0, 255, 32);
  public static boolean DRAW_BOX = false;

  /** @param bufferedImage
   * @param pixel2model with dimensions 3 x 3
   * @return */
  public static ImageRender of(BufferedImage bufferedImage, Tensor pixel2model) {
    return new ImageRender(bufferedImage, pixel2model);
  }

  /** @param bufferedImage
   * @param range vector of length 2, i.e. the extensions of the image in model coordinates */
  public static ImageRender range(BufferedImage bufferedImage, Tensor range) {
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal));
    return scale(bufferedImage, scale);
  }

  /** @param bufferedImage
   * @param scale vector of length 2 */
  public static ImageRender scale(BufferedImage bufferedImage, Tensor scale) {
    VectorQ.requireLength(scale, 2);
    return new ImageRender(bufferedImage, //
        AppendOne.FUNCTION.apply(scale.map(Scalar::reciprocal)) //
            .pmul(GfxMatrix.flipY(bufferedImage.getHeight())));
  }

  // ---
  private final BufferedImage bufferedImage;
  private final Tensor pixel2model;
  private final Tensor box;

  private ImageRender(BufferedImage bufferedImage, Tensor pixel2model) {
    this.bufferedImage = bufferedImage;
    this.pixel2model = MatrixQ.requireSize(pixel2model, 3, 3).copy();
    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();
    box = N.DOUBLE.of(Tensors.of( //
        Tensors.vector(0, 0), //
        Tensors.vector(width, 0), //
        Tensors.vector(width, height), //
        Tensors.vector(0, height)));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(pixel2model);
    graphics.drawImage(bufferedImage, AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    if (DRAW_BOX) {
      graphics.setColor(COLOR);
      graphics.draw(geometricLayer.toPath2D(box, true));
    }
    geometricLayer.popMatrix();
  }
}
