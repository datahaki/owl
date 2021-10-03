// code by jph
package ch.alpine.owl.gui;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.GeometricComponent;

/** capability for drawing in {@link GeometricComponent} */
@FunctionalInterface
public interface RenderInterface {
  /** @param geometricLayer to map model coordinates to pixel coordinates
   * @param graphics */
  void render(GeometricLayer geometricLayer, Graphics2D graphics);
}
