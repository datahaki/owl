// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Sqrt;

public class CirclesDemo extends AbstractDemo {
  @FieldSlider
  @FieldClip(min = "1", max = "20")
  public Scalar quality = RealScalar.of(10);

  public CirclesDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    for (Tensor _x : Subdivide.of(0.1, 2, 20)) {
      Scalar radius = (Scalar) _x;
      int n = Ceiling.intValueExact(Sqrt.FUNCTION.apply(radius).multiply(quality));
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(n).multiply(radius), true));
    }
  }

  public static void main(String[] args) {
    new CirclesDemo().setVisible(1000, 800);
  }
}
