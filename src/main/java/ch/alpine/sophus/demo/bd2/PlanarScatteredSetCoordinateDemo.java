// code by jph
package ch.alpine.sophus.demo.bd2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.util.PanelFieldsEditor;
import ch.alpine.javax.swing.SpinnerListener;
import ch.alpine.sophus.demo.opt.InsideConvexHullLogWeighting;
import ch.alpine.sophus.demo.opt.LogWeighting;
import ch.alpine.sophus.gds.H2Display;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class PlanarScatteredSetCoordinateDemo extends A2ScatteredSetCoordinateDemo implements SpinnerListener<ManifoldDisplay> {
  public static final Tensor BOX = Tensors.fromString("{{1, 1}, {-1, 1}, {-1, -1}, {1, -1}}");
  // ---
  private final GenesisDequeProperties dequeGenesisProperties = new GenesisDequeProperties();

  public PlanarScatteredSetCoordinateDemo() {
    super(Arrays.asList());
    spinnerLogWeighting.setVisible(false);
    Container container = timerFrame.jFrame.getContentPane();
    PanelFieldsEditor fieldsPanel = new PanelFieldsEditor(dequeGenesisProperties);
    fieldsPanel.addUniversalListener(this::recompute);
    container.add(BorderLayout.WEST, fieldsPanel.createJScrollPane());
    // ---
    ManifoldDisplay manifoldDisplay = R2Display.INSTANCE;
    actionPerformed(manifoldDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.draw(geometricLayer.toPath2D(BOX, true));
    super.render(geometricLayer, graphics);
  }

  @Override
  public void actionPerformed(ManifoldDisplay manifoldDisplay) {
    if (manifoldDisplay instanceof R2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.017, -0.953, 0.000}, {-0.991, 0.113, 0.000}, {-0.644, 0.967, 0.000}, {0.509, 0.840, 0.000}, {0.689, 0.513, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (manifoldDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.300, 0.092, 0.000}, {-0.563, -0.658, 0.262}, {-0.854, -0.200, 0.000}, {-0.746, 0.663, -0.262}, {0.467, 0.758, 0.262}, {0.446, -0.554, 0.262}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.521, 0.621, 0.262}, {-0.863, 0.258, 0.000}, {-0.725, 0.588, -0.785}, {0.392, 0.646, 0.000}, {-0.375, 0.021, 0.000}, {-0.525, -0.392, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.583, 0.338, 0.000}, {-0.904, -0.258, 0.262}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {0.396, -0.688, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.363, 0.388, 0.000}, {-0.825, -0.271, 0.000}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {-0.075, -0.733, 0.000}}"));
    } else //
    if (manifoldDisplay instanceof H2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.900, 1.783, 0.000}, {-0.083, 2.517, 0.000}, {0.500, 1.400, 0.000}, {2.300, 2.117, 0.000}, {2.833, 0.217, 0.000}, {1.000, -1.550, 0.000}, {-0.283, -0.667, 0.000}, {-1.450, -1.650, 0.000}}"));
    }
  }

  @Override
  protected LogWeighting logWeighting() {
    return new InsideConvexHullLogWeighting(dequeGenesisProperties.genesis());
  }

  public static void main(String[] args) {
    new PlanarScatteredSetCoordinateDemo().setVisible(1300, 900);
  }
}
