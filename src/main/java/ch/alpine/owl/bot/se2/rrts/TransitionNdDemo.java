// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Container;
import java.awt.Graphics2D;

import ch.alpine.java.ref.gui.ConfigPanel;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.win.ControlPointsDemo;

/** this demo maps the GeodesicDisplays
 * 
 * Clothoid -> ClothoidTransitionSpace
 * SE2 -> DubinsTransitionSpace
 * R2 -> RnTransitionSpace
 * 
 * this design is not extendable!
 * do not reproduce this design! */
public class TransitionNdDemo extends ControlPointsDemo {
  private final TransitionNdParam transitionNdParam = new TransitionNdParam();
  // ---
  private TransitionNdContainer transitionNdContainer;

  public TransitionNdDemo() {
    super(false, GeodesicDisplays.CL_SE2_R2);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    ConfigPanel configPanel = ConfigPanel.of(transitionNdParam);
    configPanel.getFieldPanels().addUniversalListener(s -> {
      System.out.println("compute udpate: " + s);
      transitionNdContainer = transitionNdParam.config();
    });
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", configPanel.getJScrollPane());
    // ---
    transitionNdContainer = transitionNdParam.config();
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    transitionNdContainer.render( //
        geodesicDisplay, //
        geometricLayer, //
        graphics, //
        geodesicDisplay.project(geometricLayer.getMouseSe2State()));
  }

  public static void main(String[] args) {
    new TransitionNdDemo().setVisible(1200, 800);
  }
}
