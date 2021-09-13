// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Container;
import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.gui.FieldsEditor;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.tensor.Tensor;

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
    super(false, ManifoldDisplays.CL_SE2_R2);
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    FieldsEditor configPanel = new FieldsEditor(transitionNdParam);
    configPanel.addUniversalListener(() -> {
      System.out.println("compute udpate");
      transitionNdContainer = transitionNdParam.config();
    });
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", configPanel.getJScrollPane());
    // ---
    transitionNdContainer = transitionNdParam.config();
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    transitionNdContainer.render( //
        manifoldDisplay, //
        geometricLayer, //
        graphics, //
        manifoldDisplay.project(mouse));
  }

  public static void main(String[] args) {
    new TransitionNdDemo().setVisible(1200, 800);
  }
}
