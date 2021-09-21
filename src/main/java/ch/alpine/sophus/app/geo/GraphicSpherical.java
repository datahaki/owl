// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.opt.nd.NdBounds;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdPair;
import ch.alpine.tensor.opt.nd.SphericalNdCluster;

public class GraphicSpherical<V> extends SphericalNdCluster<V> {
  private final GeometricLayer geometricLayer;
  private final Graphics2D graphics;

  protected GraphicSpherical( //
      NdCenterInterface ndCenterInterface, Scalar radius, //
      GeometricLayer geometricLayer, Graphics2D graphics) {
    super(ndCenterInterface, radius);
    this.geometricLayer = geometricLayer;
    this.graphics = graphics;
  }

  @Override
  public boolean isViable(NdBounds ndBounds) {
    StaticHelper.draw(ndBounds, geometricLayer, graphics);
    return super.isViable(ndBounds);
  }

  @Override
  public void consider(NdPair<V> ndPair) {
    StaticHelper.draw(ndPair.location(), geometricLayer, graphics);
    super.consider(ndPair);
  }
}
