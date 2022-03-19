// code by jph
package ch.alpine.sophus.demo.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.crv.bezier.BezierFunction;
import ch.alpine.sophus.ext.api.Curvature2DRender;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.Se2Display;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;

/** Bezier function with extrapolation */
public class BezierFunctionDemo extends AbstractCurvatureDemo {
  @FieldPreferredWidth(200)
  @FieldSlider
  @FieldInteger
  @FieldClip(min = "0", max = "10")
  public Scalar refine = RealScalar.of(6);
  public Boolean extrap = false;

  public BezierFunctionDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    addButtonDubins();
    {
      Tensor tensor = Tensors.fromString("{{1, 0, 0}, {0, 1, 0}}");
      setControlPointsSe2(tensor);
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor sequence = getGeodesicControlPoints();
    int n = sequence.length();
    if (0 == n)
      return Tensors.empty();
    int levels = refine.number().intValue();
    Tensor domain = n <= 1 //
        ? Tensors.vector(0)
        : Subdivide.of(0.0, extrap //
            ? n / (double) (n - 1)
            : 1.0, 1 << levels);
    {
      BiinvariantMean biinvariantMean = manifoldDisplay.biinvariantMean();
      if (Objects.nonNull(biinvariantMean)) {
        Tensor refined = domain.map(BezierFunction.of(biinvariantMean, sequence));
        Tensor render = Tensor.of(refined.stream().map(manifoldDisplay::toPoint));
        new PathRender(Color.RED, 1.25f).setCurve(render, false).render(geometricLayer, graphics);
      }
    }
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    Tensor refined = domain.map(BezierFunction.of(geodesicInterface, sequence));
    Tensor render = Tensor.of(refined.stream().map(manifoldDisplay::toPoint));
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(manifoldDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    LookAndFeels.DARK.updateUI();
    new BezierFunctionDemo().setVisible(1000, 600);
  }
}
