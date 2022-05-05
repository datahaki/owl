// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Graphics2D;

import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.win.RenderInterface;
import ch.alpine.sophus.crv.d2.PolygonRegion;
import ch.alpine.tensor.Tensor;

public class PolygonRegionRender implements RenderInterface {
  private final Tensor polygon;

  public PolygonRegionRender(PolygonRegion polygonRegion) {
    polygon = polygonRegion.polygon();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(RegionRenders.COLOR);
    graphics.fill(geometricLayer.toPath2D(polygon));
  }
}
