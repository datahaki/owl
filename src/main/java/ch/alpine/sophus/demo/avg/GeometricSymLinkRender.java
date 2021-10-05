// code by jph
package ch.alpine.sophus.demo.avg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.sym.SymLink;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.sca.Clips;

/** visualization of the geometric geodesic average */
/* package */ class GeometricSymLinkRender {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final int RESOLUTION = 91;
  // ---
  private final ManifoldDisplay manifoldDisplay;
  private final Geodesic geodesic;
  public int steps = 9;

  public GeometricSymLinkRender(ManifoldDisplay manifoldDisplay) {
    this.manifoldDisplay = manifoldDisplay;
    geodesic = manifoldDisplay.geodesic();
  }

  public class Link implements RenderInterface {
    private final SymLink symLink;

    public Link(SymLink symLink) {
      this.symLink = symLink;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      if (symLink.isNode())
        return;
      // ---
      new Link(symLink.lP).render(geometricLayer, graphics);
      new Link(symLink.lQ).render(geometricLayer, graphics);
      {
        Tensor posP = symLink.lP.getPosition(geodesic);
        Tensor posQ = symLink.lQ.getPosition(geodesic);
        ScalarTensorFunction scalarTensorFunction = geodesic.curve(posP, posQ);
        graphics.setColor(new Color(0, 128 + 64, 0, 255));
        {
          Tensor tensor = Subdivide.of(RealScalar.ZERO, symLink.lambda, RESOLUTION) //
              .map(scalarTensorFunction);
          Tensor points = Tensor.of(tensor.stream().map(manifoldDisplay::toPoint));
          Path2D path2d = geometricLayer.toPath2D(points);
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1f));
        }
        {
          Tensor tensor = Subdivide.of(symLink.lambda, RealScalar.ONE, RESOLUTION) //
              .map(scalarTensorFunction);
          Tensor points = Tensor.of(tensor.stream().map(manifoldDisplay::toPoint));
          Path2D path2d = geometricLayer.toPath2D(points);
          graphics.setStroke(STROKE);
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1f));
        }
        {
          Tensor tensor = Subdivide.increasing(Clips.unit(), steps).extract(1, steps) //
              .map(scalarTensorFunction);
          Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(0.5));
          graphics.setColor(new Color(64, 128 + 64, 64, 128));
          for (Tensor p : tensor) {
            geometricLayer.pushMatrix(manifoldDisplay.matrixLift(p));
            Path2D path2d = geometricLayer.toPath2D(shape);
            path2d.closePath();
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        }
      }
      // ---
      Tensor p = symLink.getPosition(geodesic);
      graphics.setColor(new Color(0, 0, 255, 192));
      geometricLayer.pushMatrix(manifoldDisplay.matrixLift(p));
      Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape().multiply(RealScalar.of(0.7)));
      path2d.closePath();
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
  }
}
