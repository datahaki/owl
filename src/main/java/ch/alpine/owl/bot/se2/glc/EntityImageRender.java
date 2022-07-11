// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.bridge.gfx.AffineTransforms;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.GfxMatrix;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.AppendOne;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.red.Times;

/** Renders an arbitrary image at the supplier state */
/* package */ class EntityImageRender implements RenderInterface {
  private final Tensor matrix;
  private final Supplier<StateTime> supplier;
  private final BufferedImage bufferedImage;

  public EntityImageRender(Supplier<StateTime> supplier, BufferedImage bufferedImage, Tensor range) {
    this.supplier = supplier;
    this.bufferedImage = bufferedImage;
    Tensor invsc = AppendOne.FUNCTION.apply(Times.of(range, //
        Tensors.vector(bufferedImage.getWidth(), -bufferedImage.getHeight()).map(Scalar::reciprocal)));
    // not generic since factor / 3 is used
    Tensor translate = GfxMatrix.translation( //
        Tensors.vector(-bufferedImage.getWidth() / 3, -bufferedImage.getHeight() / 2));
    matrix = Times.of(invsc, translate);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(GfxMatrix.of(supplier.get().state()));
    graphics.drawImage(bufferedImage, AffineTransforms.of(geometricLayer.getMatrix().dot(matrix)), null);
    geometricLayer.popMatrix();
  }
}
