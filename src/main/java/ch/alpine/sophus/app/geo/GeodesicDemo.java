// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.gui.win.BaseFrame;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.Se2Display;
import ch.alpine.sophus.gui.ren.Curvature2DRender;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;

/* package */ class GeodesicDemo extends AbstractDemo implements DemoInterface {
  private static final Color COLOR = new Color(128, 128, 128, 128);
  private static final int SPLITS = 20;
  // ---
  private final PathRender pathRender = new PathRender(new Color(128, 128, 255), //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
  private final SpinnerLabel<ManifoldDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("line");

  public GeodesicDemo() {
    List<ManifoldDisplay> list = ManifoldDisplays.ALL;
    geodesicDisplaySpinner.setList(list);
    geodesicDisplaySpinner.setValue(Se2Display.INSTANCE);
    if (1 < list.size()) {
      geodesicDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
      timerFrame.jToolBar.addSeparator();
    }
    jToggleButton.setSelected(true);
    timerFrame.jToolBar.add(jToggleButton);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    // AxesRender.INSTANCE.render(geometricLayer, graphics);
    ManifoldDisplay geodesicDisplay = geodesicDisplaySpinner.getValue();
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor xya = geometricLayer.getMouseSe2State();
    graphics.setColor(COLOR);
    Tensor q = geodesicDisplay.project(xya);
    ScalarTensorFunction scalarTensorFunction = //
        geodesicInterface.curve(geodesicDisplay.project(xya.map(Scalar::zero)), q);
    for (Tensor split : Subdivide.of(0, 1, SPLITS).map(scalarTensorFunction)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(split));
      graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape()));
      geometricLayer.popMatrix();
    }
    for (Tensor split : Subdivide.of(0, 1, 1).map(scalarTensorFunction)) {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(split));
      graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape()));
      geometricLayer.popMatrix();
    }
    if (jToggleButton.isSelected()) {
      {
        Tensor refined = Subdivide.of(0, 1, SPLITS * 6).map(scalarTensorFunction);
        Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        Curvature2DRender.of(render, false, geometricLayer, graphics);
      }
      {
        Tensor refined = Subdivide.of(1, 1.5, SPLITS * 3).map(scalarTensorFunction);
        Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        // CurveCurvatureRender.of(render, false, geometricLayer, graphics);
        pathRender.setCurve(render, false);
        pathRender.render(geometricLayer, graphics);
      }
      graphics.setColor(new Color(255, 128, 128));
      for (Tensor split : Subdivide.of(1, 1.5, SPLITS).map(scalarTensorFunction)) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(split));
        graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(0.3))));
        geometricLayer.popMatrix();
      }
    }
    RenderQuality.setDefault(graphics);
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new GeodesicDemo().setVisible(600, 600);
  }
}
