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
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Abs;

public class RnDbscanDemo extends AbstractDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  private final Tensor pointsAll = RandomVariate.of(UniformDistribution.of(0, 10), 5000, 2);
  private final DbParam dbParam = new DbParam();

  public RnDbscanDemo() {
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", new FieldsEditor(dbParam).getJScrollPane());
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.GRAY);
    Tensor points = Tensor.of(pointsAll.stream().limit(dbParam.count.number().intValue()));
    Tensor xya = timerFrame.geometricComponent.getMouseSe2CState();
    Scalar radius = Abs.FUNCTION.apply(xya.Get(2));
    Timing timing = Timing.started();
    CenterNorms centerNorms = dbParam.centerNorms;
    Integer[] labels = RnDbscan.of(points, centerNorms::ndCenterInterface, radius, dbParam.dep.number().intValue());
    double seconds = timing.seconds();
    graphics.drawString(String.format("%6.4f", seconds), 0, 40);
    
    //
    int index = 0;
    for (Tensor point : points) {
      Point2D point2d = geometricLayer.toPoint2D(point);
      Integer label = labels[index];
      graphics.setColor(label < 0 //
          ? Color.BLACK
          : COLOR_DATA_INDEXED.getColor(label));
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 10, 10);
      ++index;
    }
    {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(Se2Matrix.translation(xya.extract(0, 2)));
      graphics.draw(geometricLayer.toPath2D(centerNorms.shape().multiply(radius), true));
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new RnDbscanDemo().setVisible(1000, 800);
  }
}
