// code by jph
package ch.alpine.sophus.demo.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.r2.CirclePoints;

/* package */ enum StaticHelper {
  ;
  static final Tensor SE2 = Tensors.fromString("{{180, 0, 6}, {0, -180, 186}, {0, 0, 1}}").unmodifiable();
  static final Tensor POINT = CirclePoints.of(10).multiply(RealScalar.of(0.015)).unmodifiable();
  static final Tensor SE2_2 = Tensors.fromString("{{180*2, 0, 6*2}, {0, -180*2, 186*2}, {0, 0, 1}}").unmodifiable();

  static BufferedImage createWhite(int size) {
    BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, size, size);
    return bufferedImage;
  }

  static BufferedImage createWhite() {
    return createWhite(192);
  }
}
