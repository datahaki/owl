// code by jph
package ch.alpine.ubongo;

import java.awt.Dimension;
import java.awt.Graphics2D;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.swing.LookAndFeels;
import ch.alpine.bridge.swing.SpinnerLabel;

/* package */ class UbongoViewer extends AbstractDemo {
  // ---
  private final SpinnerLabel<UbongoPublish> spinnerLabel = SpinnerLabel.of(UbongoPublish.class);

  public UbongoViewer() {
    spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "index");
    spinnerLabel.setValue(UbongoPublish.STANDARD);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    StaticHelper.draw(graphics, spinnerLabel.getValue(), StaticHelper.SCALE);
  }

  public static void main(String[] args) {
    LookAndFeels.LIGHT.updateComponentTreeUI();
    UbongoViewer ubongoViewer = new UbongoViewer();
    ubongoViewer.setVisible(1200, 600);
  }
}
