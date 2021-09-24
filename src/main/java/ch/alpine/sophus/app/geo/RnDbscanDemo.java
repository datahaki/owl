// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.gui.FieldsEditor;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.lie.rn.RnDbscan;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.opt.nd.NdCenterBase;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Abs;

public class RnDbscanDemo extends AbstractDemo {
  private final Tensor pointsAll = RandomVariate.of(UniformDistribution.of(0, 10), 5000, 2);
  public final NdParam ndParam = new NdParam();

  public RnDbscanDemo() {
    ndParam.count = RealScalar.of(50);
    // ---
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", new FieldsEditor(ndParam).getJScrollPane());
    timerFrame.geometricComponent.setOffset(100, 600);
    // ---
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.GRAY);
    Tensor points = Tensor.of(pointsAll.stream().limit(ndParam.count.number().intValue()));
    Tensor xya = timerFrame.geometricComponent.getMouseSe2CState();
    Scalar radius = Abs.FUNCTION.apply(xya.Get(2));
    Timing timing = Timing.started();
    RnDbscan rnDbscan = new RnDbscan(radius, ndParam.dep.number().intValue());
    Integer[] labels = rnDbscan.cluster(points, NdCenterBase::of2Norm);
    double seconds = timing.seconds();
    graphics.drawString(String.format("%6.4f", seconds), 0, 40);
    ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
    //
    int index = 0;
    for (Tensor point : points) {
      Point2D point2d = geometricLayer.toPoint2D(point);
      Integer label = labels[index];
      graphics.setColor(label < 0 //
          ? Color.BLACK
          : colorDataIndexed.getColor(label));
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 10, 10);
      ++index;
    }
    {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(Se2Matrix.translation(xya.extract(0, 2)));
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(40).multiply(radius), true));
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new RnDbscanDemo().setVisible(1000, 800);
  }
}
