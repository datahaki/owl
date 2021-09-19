// code by jph
package ch.alpine.sophus.app.geo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collection;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Timing;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.opt.nd.EuclideanNdCenter;
import ch.alpine.tensor.opt.nd.NdMap;
import ch.alpine.tensor.opt.nd.NdMatch;
import ch.alpine.tensor.opt.nd.NdTreeMap;
import ch.alpine.tensor.opt.nd.SphericalNdCluster;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Abs;

public class SphericalNdClusterDemo extends AbstractDemo {
  private final Tensor points = RandomVariate.of(UniformDistribution.of(0, 10), 500, 2);
  private final NdMap<Object> ndMap;

  public SphericalNdClusterDemo() {
    ndMap = new NdTreeMap<>(Tensors.vector(0, 0), Tensors.vector(10, 10), 5, 10);
    for (Tensor point : points) {
      ndMap.add(point, null);
    }
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    graphics.setColor(Color.GRAY);
    for (Tensor point : points) {
      Point2D point2d = geometricLayer.toPoint2D(point);
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 2, 2);
    }
    Tensor xya = timerFrame.geometricComponent.getMouseSe2CState();
    Scalar radius = Abs.FUNCTION.apply(xya.Get(2));
    Timing timing = Timing.started();
    Collection<NdMatch<Object>> collection = SphericalNdCluster.of(ndMap, EuclideanNdCenter.of(xya.extract(0, 2)), radius);
    double seconds = timing.seconds();
    graphics.drawString(String.format("%d %6.4f", collection.size(), seconds), 0, 40);
    graphics.setColor(new Color(255, 0, 0, 128));
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
    new SphericalNdClusterDemo().setVisible(1000, 800);
  }
}
