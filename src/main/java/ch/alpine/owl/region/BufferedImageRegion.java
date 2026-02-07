// code by jph
package ch.alpine.owl.region;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.Serializable;

import ch.alpine.ascony.ren.ImageRender;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;

/** region in R2 */
public class BufferedImageRegion implements Region<Tensor>, RegionBounds, RenderInterface, Serializable {
  private transient final BufferedImage bufferedImage;
  private final CoordinateBoundingBox coordinateBoundingBox;
  private final ImageRender imageRender;
  private transient final AffineFrame affineFrame;
  private final Tensor pixel2model;
  private final int width;
  private final int height;
  private final byte[] data;
  private final boolean outside;

  /** @param bufferedImage of type BufferedImage.TYPE_BYTE_GRAY
   * @param pixel2model with dimension 3 x 3
   * @param outside membership */
  public BufferedImageRegion(BufferedImage bufferedImage, CoordinateBoundingBox coordinateBoundingBox, boolean outside) {
    if (bufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY)
      throw new IllegalArgumentException("" + bufferedImage.getType());
    this.bufferedImage = bufferedImage;
    this.coordinateBoundingBox = coordinateBoundingBox;
    imageRender = new ImageRender(bufferedImage, coordinateBoundingBox);
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    this.pixel2model = ImageRender.pixel2model(coordinateBoundingBox, width, height);
    affineFrame = new AffineFrame(Inverse.of(pixel2model));
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    this.outside = outside;
  }

  @Override // from Region
  public final boolean test(Tensor vector) {
    return isMember( //
        vector.Get(0).number().doubleValue(), //
        vector.Get(1).number().doubleValue());
  }

  public boolean isMember(double px, double py) {
    int x = (int) affineFrame.toX(px, py);
    if (0 <= x && x < width) {
      int y = (int) affineFrame.toY(px, py);
      if (0 <= y && y < height)
        return data[y * width + x] != 0;
    }
    return outside;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    imageRender.render(geometricLayer, graphics);
  }

  /** @return bufferedImage of type BufferedImage.TYPE_BYTE_GRAY */
  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public Tensor pixel2model() {
    return pixel2model.unmodifiable();
  }

  @Override
  public CoordinateBoundingBox coordinateBounds() {
    return coordinateBoundingBox;
  }
}
