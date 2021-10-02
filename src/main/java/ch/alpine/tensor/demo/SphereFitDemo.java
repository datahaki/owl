// code by jph
package ch.alpine.tensor.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Optional;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.sophus.fit.HsWeiszfeldMethod;
import ch.alpine.sophus.fit.SpatialMedian;
import ch.alpine.sophus.fit.SphereFit;
import ch.alpine.sophus.fit.WeiszfeldMethod;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.gui.win.DubinsGenerator;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.MetricBiinvariant;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.math.var.InversePowerVariogram;
import ch.alpine.sophus.ply.StarPoints;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.lie.r2.ConvexHull;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.opt.hun.BipartiteMatching;
import ch.alpine.tensor.sca.Chop;

/* package */ class SphereFitDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  private static final Tensor CIRCLE = CirclePoints.of(10).multiply(RealScalar.of(3));
  // ---
  private final PathRender pathRenderBall = new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f);
  private final PathRender pathRenderHull = new PathRender(COLOR_DATA_INDEXED.getColor(1), 1.5f);

  public SphereFitDemo() {
    super(false, ManifoldDisplays.R2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterface(pathRenderHull);
    // ---
    Tensor blub = Tensors.fromString(
        "{{1, 0, 0}, {1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}, {4, 0, 3.14159}, {2, 0, 3.14159}, {2, 0, 0}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(blub.stream().map(Tensors.vector(2, 1, 1)::pmul))));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    final ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor control = getGeodesicControlPoints();
    {
      Optional<SphereFit> optional = SphereFit.of(control);
      if (optional.isPresent()) {
        Tensor center = optional.get().center();
        Scalar radius = optional.get().radius();
        geometricLayer.pushMatrix(Se2Matrix.translation(center));
        pathRenderBall.setCurve(CirclePoints.of(40).multiply(radius), true);
        pathRenderBall.render(geometricLayer, graphics);
        geometricLayer.popMatrix();
      }
    }
    pathRenderHull.setCurve(ConvexHull.of(control), true);
    {
      new PathRender(Color.GRAY).setCurve(CIRCLE, true).render(geometricLayer, graphics);
      Tensor matrix = Tensors.matrix((i, j) -> //
      Vector2Norm.between(control.get(i), CIRCLE.get(j)), control.length(), CIRCLE.length());
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
      int[] matching = bipartiteMatching.matching();
      graphics.setColor(Color.RED);
      for (int index = 0; index < matching.length; ++index)
        if (matching[index] != BipartiteMatching.UNASSIGNED) {
          Path2D path2d = geometricLayer.toPath2D(Tensors.of(control.get(index), CIRCLE.get(matching[index])));
          graphics.draw(path2d);
        }
    }
    {
      Tensor weiszfeld = WeiszfeldMethod.with(Chop._04).uniform(control).get();
      geometricLayer.pushMatrix(Se2Matrix.translation(weiszfeld));
      Path2D path2d = geometricLayer.toPath2D(manifoldDisplay.shape());
      path2d.closePath();
      graphics.setColor(new Color(128, 128, 255, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(128, 128, 255, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    {
      Biinvariant biinvariant = MetricBiinvariant.EUCLIDEAN;
      TensorUnaryOperator weightingInterface = //
          biinvariant.weighting(manifoldDisplay.hsManifold(), InversePowerVariogram.of(1), control);
      SpatialMedian spatialMedian = HsWeiszfeldMethod.of(manifoldDisplay.biinvariantMean(), weightingInterface, Chop._06);
      Optional<Tensor> optional = spatialMedian.uniform(control);
      if (optional.isPresent()) {
        Tensor weiszfeld = optional.get();
        geometricLayer.pushMatrix(Se2Matrix.translation(weiszfeld));
        Path2D path2d = geometricLayer.toPath2D(StarPoints.of(5, 0.2, 0.05));
        path2d.closePath();
        graphics.setColor(new Color(128, 128, 255, 64));
        graphics.fill(path2d);
        graphics.setColor(new Color(128, 128, 255, 255));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new SphereFitDemo().setVisible(1000, 600);
  }
}
