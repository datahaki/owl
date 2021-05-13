// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.decim.HsLineDistance;
import ch.alpine.sophus.decim.HsLineDistance.NormImpl;
import ch.alpine.sophus.decim.HsLineProjection;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.sophus.math.Exponential;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.ply.Arrowhead;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.sca.Round;

/* package */ class Se2LineDistanceDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(182);
  // ---
  private final JToggleButton axes = new JToggleButton("axes");

  public Se2LineDistanceDemo() {
    super(false, ManifoldDisplays.SE2_ONLY);
    timerFrame.jToolBar.add(axes);
    Tensor tensor = Tensors.fromString("{{0, 0, 0}, {5, 0, 1}}");
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getControlPointsSe2();
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    Exponential exponential = Se2CoveringExponential.INSTANCE;
    // ---
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor beg = sequence.get(0);
    Tensor end = sequence.get(1);
    ScalarTensorFunction curve = geodesicInterface.curve(beg, end);
    {
      Tensor tensor = Subdivide.of(-0.5, 1.5, 55).map(curve);
      Path2D path2d = geometricLayer.toPath2D(Tensor.of(tensor.stream().map(geodesicDisplay::toPoint)));
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
    }
    final Tensor mouse = geometricLayer.getMouseSe2State();
    {
      HsLineDistance hsLineDistance = new HsLineDistance(geodesicDisplay.hsManifold());
      NormImpl normImpl = hsLineDistance.tensorNorm(beg, end);
      {
        Tensor project = normImpl.project(mouse);
        Tensor exp = exponential.exp(project);
        Tensor glb = lieGroup.element(beg).combine(exp);
        {
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(glb));
          Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.4));
          path2d.closePath();
          graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(2));
          graphics.fill(path2d);
          graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(2));
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
      }
      {
        Tensor orthogonal = normImpl.orthogonal(mouse);
        Tensor exp = exponential.exp(orthogonal);
        Tensor glb = lieGroup.element(beg).combine(exp);
        {
          graphics.setColor(Color.DARK_GRAY);
          graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
          graphics.drawString("" + orthogonal.map(Round._3), 0, 40);
        }
        {
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(glb));
          Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.4));
          path2d.closePath();
          graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(3));
          graphics.fill(path2d);
          graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(3));
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
      }
    }
    {
      HsLineProjection hsLineProjection = new HsLineProjection(geodesicDisplay.hsManifold());
      Tensor onto = hsLineProjection.onto(beg, end, mouse);
      {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(onto));
        Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.4));
        path2d.closePath();
        graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(4));
        graphics.fill(path2d);
        graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(4));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    // ---
    {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(curve.apply(RationalScalar.HALF)));
      Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.5));
      path2d.closePath();
      graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
      graphics.fill(path2d);
      graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new Se2LineDistanceDemo().setVisible(1200, 600);
  }
}
