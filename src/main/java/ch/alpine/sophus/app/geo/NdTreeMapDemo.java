// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Optional;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.gui.FieldsEditor;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.opt.nd.EuclideanNdCenter;
import ch.alpine.tensor.opt.nd.NdCenterInterface;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;

public class NdTreeMapDemo extends AbstractDemo {
  private final Tensor points = RandomVariate.of(UniformDistribution.of(0, 10), 500, 2);
  public final NdParam ndParam = new NdParam();

  public NdTreeMapDemo() {
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", new FieldsEditor(ndParam).getJScrollPane());
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.GRAY);
    for (Tensor point : points) {
      Point2D point2d = geometricLayer.toPoint2D(point);
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 2, 2);
    }
    Tensor xya = timerFrame.geometricComponent.getMouseSe2CState();
    Scalar radius = Abs.FUNCTION.apply(xya.Get(2));
    NdMap<Object> ndMap = new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(10, 10), //
        ndParam.dep.number().intValue(), //
        ndParam.max.number().intValue());
    for (Tensor point : points)
      ndMap.add(point, null);
    Timing timing = Timing.started();
    NdCenterInterface ndCenterInterface = EuclideanNdCenter.of(xya.extract(0, 2));
    int limit = ndParam.pCount.number().intValue();
    final Collection<NdMatch<Object>> collection;
    graphics.setColor(new Color(128, 128, 128, 64));
    if (ndParam.nearest) {
      GraphicNearest<Object> graphicNearest = //
          new GraphicNearest<>(ndCenterInterface, limit, geometricLayer, graphics);
      ndMap.visit(graphicNearest);
      collection = graphicNearest.queue();
    } else {
      GraphicSpherical<Object> graphicSpherical = //
          new GraphicSpherical<>(ndCenterInterface, radius, geometricLayer, graphics);
      ndMap.visit(graphicSpherical);
      collection = graphicSpherical.list();
      // collection = SphericalNdCluster.of(ndMap, ndCenterInterface, radius);
    }
    double seconds = timing.seconds();
    graphics.drawString(String.format("%d %6.4f", collection.size(), seconds), 0, 40);
    graphics.setColor(new Color(255, 0, 0, 128));
    if (ndParam.nearest) {
      Optional<Scalar> optional = collection.stream() //
          .map(NdMatch::distance) //
          .reduce(Max::of);
      if (optional.isPresent())
        radius = optional.get();
    }
    {
      geometricLayer.pushMatrix(Se2Matrix.of(xya));
      graphics.draw(geometricLayer.toPath2D(CirclePoints.of(40).multiply(radius), true));
      geometricLayer.popMatrix();
    }
    graphics.setColor(Color.RED);
    for (NdMatch<Object> ndMatch : collection) {
      Tensor point = ndMatch.location();
      Point2D point2d = geometricLayer.toPoint2D(point);
      graphics.fillRect((int) point2d.getX() - 1, (int) point2d.getY() - 1, 4, 4);
    }
  }

  public static void main(String[] args) {
    new NdTreeMapDemo().setVisible(1000, 800);
  }
}
