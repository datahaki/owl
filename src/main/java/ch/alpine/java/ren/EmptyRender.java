// code by jph
package ch.alpine.java.ren;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;

public enum EmptyRender implements RenderInterface {
  INSTANCE;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // ---
  }
}
