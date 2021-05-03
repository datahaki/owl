// code by jph
package ch.alpine.owl.gui;

import java.awt.Graphics2D;

import ch.alpine.owl.gui.win.GeometricComponent;
import ch.alpine.owl.gui.win.GeometricLayer;

/** capability for drawing in {@link GeometricComponent} */
@FunctionalInterface
public interface RenderInterface {
  /** @param geometricLayer to map model coordinates to pixel coordinates
   * @param graphics */
  void render(GeometricLayer geometricLayer, Graphics2D graphics);
}
