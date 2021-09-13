// code by jph
package ch.alpine.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.awt.SpinnerListener;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.sophus.gds.Se2AbstractDisplay;
import ch.alpine.sophus.gds.Se2CoveringDisplay;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorDataGradients;

/* package */ class GrassmannDemo extends LogWeightingDemo implements SpinnerListener<ManifoldDisplay> {
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public GrassmannDemo() {
    super(true, ManifoldDisplays.SE2C_SE2_S2_H2_R2, LogWeightings.list());
    // ---
    spinnerColorData.setValue(ColorDataGradients.TEMPERATURE);
    spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    // ---
    timerFrame.jToolBar.add(jToggleNeutral);
    // ---
    ManifoldDisplay manifoldDisplay = Se2CoveringDisplay.INSTANCE;
    manifoldDisplay = S2Display.INSTANCE;
    setGeodesicDisplay(manifoldDisplay);
    setBitype(Bitype.LEVERAGES1);
    actionPerformed(manifoldDisplay);
    addSpinnerListener(this);
    jToggleNeutral.setSelected(true);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      LeversRender leversRender = //
          LeversRender.of(manifoldDisplay, sequence, origin, geometricLayer, graphics);
      ColorDataGradient colorDataGradient = spinnerColorData.getValue().deriveWithOpacity(RealScalar.of(0.5));
      LeversHud.render(bitype(), leversRender, colorDataGradient);
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay manifoldDisplay) {
    if (manifoldDisplay instanceof R2Display) {
      setControlPointsSe2(R2PointCollection.SOME);
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
    if (manifoldDisplay instanceof Se2AbstractDisplay) {
      setControlPointsSe2(Tensors.fromString(
          "{{3.150, -2.700, -0.524}, {-1.950, -3.683, 0.000}, {-1.500, -1.167, 2.094}, {4.533, -0.733, -1.047}, {8.567, -3.300, -1.309}, {2.917, -5.050, -1.047}}"));
    }
  }

  public static void main(String[] args) {
    new GrassmannDemo().setVisible(1200, 900);
  }
}
