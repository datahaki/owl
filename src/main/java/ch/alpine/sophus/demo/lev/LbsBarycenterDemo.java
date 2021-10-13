// code by jph
package ch.alpine.sophus.demo.lev;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerListener;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.sophus.demo.opt.PolygonCoordinates;
import ch.alpine.sophus.gds.GeodesicDisplayRender;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.S2Display;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** Visualization of
 * "Spherical Barycentric Coordinates"
 * by Torsten Langer, Alexander Belyaev, Hans-Peter Seidel, 2005 */
/* package */ class LbsBarycenterDemo extends LogWeightingDemo implements SpinnerListener<ManifoldDisplay> {
  private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public LbsBarycenterDemo() {
    super(true, ManifoldDisplays.S2_ONLY, Arrays.asList(PolygonCoordinates.values()));
    // ---
    timerFrame.jToolBar.add(jToggleNeutral);
    // ---
    ManifoldDisplay manifoldDisplay = S2Display.INSTANCE;
    setGeodesicDisplay(manifoldDisplay);
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
      // ---
      leversRender.renderSurfaceP();
      leversRender.renderSequence();
      leversRender.renderOrigin();
      leversRender.renderLbsS2();
      leversRender.renderLevers();
      leversRender.renderIndexX();
      leversRender.renderIndexP();
      // ---
      geometricLayer.pushMatrix(GfxMatrix.translation(Tensors.vector(3, 0)));
      GeodesicDisplayRender.render_s2(geometricLayer, graphics);
      // ---
      leversRender.renderSurfaceP();
      leversRender.renderSequence();
      leversRender.renderOrigin();
      leversRender.renderTangentsXtoP(false);
      leversRender.renderPolygonXtoP();
      leversRender.renderLevers();
      leversRender.renderIndexX();
      leversRender.renderIndexP();
      // ---
      geometricLayer.popMatrix();
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay manifoldDisplay) {
    if (manifoldDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.314, 0.662, 0.000}, {-0.809, 0.426, 0.000}, {-0.261, 0.927, 0.000}, {0.564, 0.685, 0.000}, {0.694, 0.220, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new LbsBarycenterDemo().setVisible(1200, 900);
  }
}
