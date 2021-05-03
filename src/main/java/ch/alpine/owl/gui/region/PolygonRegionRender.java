// code by jph
package ch.alpine.owl.gui.region;

import java.awt.Graphics2D;

import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.owl.math.region.PolygonRegion;
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
