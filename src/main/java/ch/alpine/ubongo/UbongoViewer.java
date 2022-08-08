// code by jph
package ch.alpine.ubongo;

import java.awt.Graphics2D;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.bridge.swing.LookAndFeels;

/* package */ class UbongoViewer extends AbstractDemo {
  @ReflectionMarker
  public static class Param {
    public UbongoPublish ubongoPublish = UbongoPublish.STARFIS3;
  }

  private final Param param;

  public UbongoViewer() {
    this(new Param());
  }

  public UbongoViewer(Param param) {
    super(param);
    this.param = param;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    StaticHelper.draw(graphics, param.ubongoPublish, StaticHelper.SCALE);
  }

  public static void main(String[] args) {
    LookAndFeels.LIGHT.updateComponentTreeUI();
    UbongoViewer ubongoViewer = new UbongoViewer();
    ubongoViewer.setVisible(700, 900);
  }
}
