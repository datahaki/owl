// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;

public enum EmptyRender implements RenderInterface {
  INSTANCE;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // ---
  }
}
