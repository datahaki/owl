// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.pow.Sqrt;

public class CirclesDemo extends AbstractDemo {
  @FieldSlider
  @FieldClip(min = "1", max = "20")
  public Scalar quality = RealScalar.of(10);
  public Boolean plot = true;

  public CirclesDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    VisualSet visualSet = new VisualSet();
    for (Tensor _x : Subdivide.of(0.1, 2, 20)) {
      Scalar radius = (Scalar) _x;
      int n = Math.max(2, Ceiling.intValueExact(Sqrt.FUNCTION.apply(radius).multiply(quality)));
      Tensor curve = CirclePoints.of(n).multiply(radius);
      graphics.draw(geometricLayer.toPath2D(curve, true));
      if (plot)
        visualSet.add(Subdivide.increasing(Clips.unit(), curve.length() - 1), //
            StaticHelper.TRIPLE_REDUCE_EXTRAPOLATION.apply( //
                curve));
    }
    if (plot) {
      JFreeChart jFreeChart = ListPlot.of(visualSet, true);
      jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 400, 300));
    }
  }

  public static void main(String[] args) {
    new CirclesDemo().setVisible(1000, 800);
  }
}
