// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldSelection;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.gui.FieldsToolbar;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.itp.BSplineFunction;
import ch.alpine.tensor.itp.BSplineFunctionString;
import ch.alpine.tensor.itp.DeBoor;
import ch.alpine.tensor.mat.re.Inverse;

/* package */ class DeBoorDemo extends AbstractDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final Color TICKS_COLOR = new Color(0, 0, 0, 128);

  // ---
  @ReflectionMarker
  public static class Param {
    @FieldSelection(array = { "0", "1", "2", "3", "4", "5", "6" })
    public Scalar degree = RealScalar.of(1);
  }

  private final Param param = new Param();

  public DeBoorDemo() {
    FieldsToolbar.add(param, timerFrame.jToolBar);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    int degree = param.degree.number().intValue();
    {
      graphics.setStroke(new BasicStroke(1.25f));
      Tensor matrix = geometricLayer.getMatrix();
      geometricLayer.pushMatrix(Inverse.of(matrix));
      {
        graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        for (int length = 2; length <= 6; ++length) {
          Tensor string = Tensors.fromString("{{200, 0, 0}, {0, -180, 0}, {0, 0, 1}}");
          string.set(RealScalar.of(210 * (length - 1)), 1, 2);
          geometricLayer.pushMatrix(string);
          Tensor domain = Subdivide.of(0, length - 1, (length - 1) * 20);
          {
            for (int k_th = 0; k_th < length; ++k_th) {
              graphics.setColor(COLOR_DATA_INDEXED.getColor(k_th));
              BSplineFunction bSplineFunction = (BSplineFunction) BSplineFunctionString.of(degree, UnitVector.of(length, k_th));
              DeBoor deBoor = bSplineFunction.deBoor(k_th);
              Tensor knots = deBoor.knots();
              Point2D point2d = geometricLayer.toPoint2D(k_th, 0);
              graphics.drawString(length + " " + k_th + ":" + knots.toString().replace(" ", ""), //
                  (int) point2d.getX(), //
                  (int) point2d.getY() + 10);
              Tensor values = domain.map(bSplineFunction);
              Tensor tensor = Transpose.of(Tensors.of(domain, values));
              graphics.draw(geometricLayer.toPath2D(tensor));
              graphics.setColor(TICKS_COLOR);
              graphics.draw(geometricLayer.toPath2D(Tensors.matrix(new Number[][] { { k_th, 0 }, { k_th, .1 } })));
            }
          }
          geometricLayer.popMatrix();
        }
      }
      geometricLayer.popMatrix();
      graphics.setStroke(new BasicStroke(1f));
    }
    graphics.setColor(new Color(255, 128, 128, 255));
  }

  public static void main(String[] args) {
    new DeBoorDemo().setVisible(1000, 800);
  }
}
