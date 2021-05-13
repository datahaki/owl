// code by jph
package ch.alpine.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.lev.LogWeightingDemo;
import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.lie.rn.RnManifold;
import ch.alpine.sophus.opt.LogWeightings;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorUnaryOperator;

/* package */ class BarycentricExtrapolationDemo extends LogWeightingDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public BarycentricExtrapolationDemo() {
    super(true, ManifoldDisplays.SE2C_R2, LogWeightings.list());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor sequence = getGeodesicControlPoints();
    int length = sequence.length();
    Tensor domain = Range.of(-sequence.length(), 0).map(Tensors::of).unmodifiable();
    graphics.setColor(Color.GRAY);
    graphics.setStroke(STROKE);
    for (int index = 0; index < length; ++index) {
      Line2D line2d = geometricLayer.toLine2D( //
          domain.get(index).append(RealScalar.ZERO), //
          manifoldDisplay.toPoint(sequence.get(index)));
      graphics.draw(line2d);
    }
    graphics.setStroke(new BasicStroke());
    if (1 < length) {
      Tensor samples = Subdivide.of(-length, 0, 127).map(Tensors::of);
      BiinvariantMean biinvariantMean = manifoldDisplay.biinvariantMean();
      TensorUnaryOperator tensorUnaryOperator = operator(RnManifold.INSTANCE, domain);
      Tensor curve = Tensor.of(samples.stream() //
          .map(tensorUnaryOperator) //
          .map(weights -> biinvariantMean.mean(sequence, weights)));
      new PathRender(Color.BLUE, 1.5f) //
          .setCurve(curve, false) //
          .render(geometricLayer, graphics);
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BarycentricExtrapolationDemo().setVisible(1200, 600);
  }
}
