// code by jph
package ch.alpine.tensor.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.ascona.lev.LeversRender;
import ch.alpine.ascona.util.api.ControlPointsDemo;
import ch.alpine.ascona.util.dis.ManifoldDisplays;
import ch.alpine.ascona.util.ren.PathRender;
import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Outer;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.opt.hun.BipartiteMatching;

/* package */ class BipartiteMatchingDemo extends ControlPointsDemo {
  private static final Tensor CIRCLE = CirclePoints.of(5).multiply(RealScalar.of(3));

  public BipartiteMatchingDemo() {
    super(true, ManifoldDisplays.R2_ONLY);
    // ---
    setControlPointsSe2(Tensors.fromString("{{1, 0, 0}, {0, 1, 0}, {1, 1, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    if (0 < control.length()) {
      new PathRender(Color.GRAY).setCurve(CIRCLE, true).render(geometricLayer, graphics);
      Tensor matrix = Outer.of(Vector2Norm::between, control, CIRCLE);
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
      LeversRender leversRender = LeversRender.of(manifoldDisplay(), control, null, geometricLayer, graphics);
      leversRender.renderSequence();
    }
  }

  public static void main(String[] args) {
    new BipartiteMatchingDemo().setVisible(1000, 600);
  }
}
