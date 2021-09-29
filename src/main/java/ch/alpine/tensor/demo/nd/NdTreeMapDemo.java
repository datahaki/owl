// code by jph
package ch.alpine.tensor.demo.nd;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Optional;

import javax.swing.JScrollPane;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.gui.FieldsEditor;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.math.MinMax;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;

public class NdTreeMapDemo extends AbstractDemo {
  private final Tensor pointsAll = RandomVariate.of(UniformDistribution.of(0, 10), 5000, 2);
  private final NdParam ndParam = new NdParam();

  public NdTreeMapDemo() {
    Container container = timerFrame.jFrame.getContentPane();
    JScrollPane jScrollPane = new FieldsEditor(ndParam).getJScrollPane();
    jScrollPane.setPreferredSize(new Dimension(200, 200));
    container.add("West", jScrollPane);
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.GRAY);
    Tensor points = Tensor.of(pointsAll.stream().limit(ndParam.count.number().intValue()));
    for (Tensor point : points) {
      Point2D point2d = geometricLayer.toPoint2D(point);
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 2, 2);
    }
    Tensor xya = timerFrame.geometricComponent.getMouseSe2CState();
    Scalar radius = Abs.FUNCTION.apply(xya.Get(2).multiply(RealScalar.of(0.3)));
    NdMap<Void> ndMap = NdTreeMap.of(MinMax.ndBox(points), ndParam.dep.number().intValue());
    for (Tensor point : points)
      ndMap.insert(point, null);
    Timing timing = Timing.started();
    CenterNorms centerNorms = ndParam.centerNorms;
    NdCenterInterface ndCenterInterface = centerNorms.ndCenterInterface(xya.extract(0, 2));
    int limit = ndParam.pCount.number().intValue();
    final Collection<NdMatch<Void>> collection;
    if (ndParam.nearest) {
      GraphicNearest<Void> graphicNearest = //
          new GraphicNearest<>(ndCenterInterface, limit, geometricLayer, graphics);
      ndMap.visit(graphicNearest);
      collection = graphicNearest.queue();
    } else {
      GraphicSpherical<Void> graphicSpherical = //
          new GraphicSpherical<>(ndCenterInterface, radius, geometricLayer, graphics);
      ndMap.visit(graphicSpherical);
      collection = graphicSpherical.list();
    }
    double seconds = timing.seconds();
    graphics.drawString(String.format("%d %6.4f", collection.size(), seconds), 0, 40);
    graphics.setColor(new Color(255, 0, 0, 128));
    if (ndParam.nearest) {
      Optional<Scalar> optional = collection.stream() //
          .map(NdMatch::distance) //
          .reduce(Max::of);
      if (optional.isPresent())
        radius = optional.orElseThrow();
    }
    {
      graphics.setColor(Color.BLUE);
      geometricLayer.pushMatrix(Se2Matrix.translation(xya.extract(0, 2)));
      graphics.draw(geometricLayer.toPath2D(centerNorms.shape().multiply(radius), true));
      geometricLayer.popMatrix();
    }
    graphics.setColor(new Color(0, 128, 0, 255));
    for (NdMatch<Void> ndMatch : collection) {
      Tensor point = ndMatch.location();
      Point2D point2d = geometricLayer.toPoint2D(point);
      graphics.fillRect((int) point2d.getX() - 1, (int) point2d.getY() - 1, 4, 4);
    }
  }

  public static void main(String[] args) {
    new NdTreeMapDemo().setVisible(1000, 800);
  }
}
