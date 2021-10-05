// code by jph
package ch.alpine.sophus.demo.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Optional;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.awt.SpinnerListener;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.sophus.gbc.d2.IterativeCoordinateMatrix;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.sophus.gds.Se2AbstractDisplay;
import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.hs.VectorLogManifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ class IterativeCoordinateDemo extends LogWeightingDemo implements SpinnerListener<ManifoldDisplay> {
  private final SpinnerLabel<Integer> spinnerTotal = new SpinnerLabel<>();
  // private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public IterativeCoordinateDemo() {
    super(true, ManifoldDisplays.R2_ONLY, LogWeightings.list());
    // ---
    spinnerTotal.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 50, 100));
    spinnerTotal.setValue(2);
    // spinnerTotal.addSpinnerListener(this::config);
    spinnerTotal.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "total");
    // ---
    ManifoldDisplay manifoldDisplay = R2Display.INSTANCE;
    setGeodesicDisplay(manifoldDisplay);
    setBitype(Bitype.LEVERAGES1);
    actionPerformed(manifoldDisplay);
    addSpinnerListener(this);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
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
      leversRender.renderSurfaceP();
      LeversHud.render(bitype(), leversRender, null);
      VectorLogManifold vectorLogManifold = manifoldDisplay.hsManifold();
      HsDesign hsDesign = new HsDesign(vectorLogManifold);
      try {
        Tensor matrix = IterativeCoordinateMatrix.of(spinnerTotal.getValue()).origin(hsDesign.matrix(sequence, origin));
        Tensor circum = matrix.dot(sequence);
        // new PointsRender(color_fill, color_draw).show(matrixLift, shape, points);
        // new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255)) //
        // .show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), circum) //
        // .render(geometricLayer, graphics);
        leversRender.renderMatrix2(origin, matrix);
        LeversRender lr2 = LeversRender.of(manifoldDisplay, circum, origin, geometricLayer, graphics);
        lr2.renderSequence();
        lr2.renderIndexP("c");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      }
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
    new IterativeCoordinateDemo().setVisible(1200, 900);
  }
}
