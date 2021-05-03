// code by jph
package ch.alpine.sophus.app.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidContext;
import ch.alpine.sophus.clt.ClothoidEmit;
import ch.alpine.sophus.clt.ClothoidSolutions;
import ch.alpine.sophus.clt.ClothoidSolutions.Search;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ class ClothoidDefectContainer implements RenderInterface {
  private static final Clip RANGE = Clips.absolute(15.0);
  private static final Scalar DENOM = RealScalar.of(5.0);
  private static final ClothoidSolutions CLOTHOID_SOLUTIONS = ClothoidSolutions.of(RANGE);
  // ---
  public final Search search;
  public final ClothoidContext clothoidContext;

  public ClothoidDefectContainer(ClothoidContext clothoidContext) {
    search = CLOTHOID_SOLUTIONS.new Search(clothoidContext.s1(), clothoidContext.s2());
    this.clothoidContext = clothoidContext;
  }

  public boolean encodes(ClothoidContext clothoidContext) {
    return this.clothoidContext.s1().equals(clothoidContext.s1()) //
        && this.clothoidContext.s2().equals(clothoidContext.s2());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    PathRender pathRender = new PathRender(new Color(0, 0, 0, 128));
    Tensor tensor = Transpose.of(Tensors.of(CLOTHOID_SOLUTIONS.probes, search.defects_real));
    pathRender.setCurve(tensor, false);
    pathRender.render(geometricLayer, graphics);
    Tensor lambdas = search.lambdas();
    List<Clothoid> clothoids = ClothoidEmit.stream(clothoidContext, lambdas).collect(Collectors.toList());
    for (int index = 0; index < lambdas.length(); ++index) {
      Scalar lambda = lambdas.Get(index);
      Clothoid clothoid = clothoids.get(index);
      {
        Scalar length = clothoid.length().divide(DENOM);
        graphics.setColor(new Color(0, 128, 0));
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(lambda, length.zero()), Tensors.of(lambda, length)));
        graphics.setStroke(new BasicStroke(1f));
      }
      {
        Scalar length = clothoid.length();
        graphics.setColor(new Color(0, 128, 0));
        graphics.setStroke(new BasicStroke(2f));
        Scalar x = lambda.add(RealScalar.of(0.1));
        graphics.draw(geometricLayer.toLine2D(Tensors.of(x, length.zero()), Tensors.of(x, length)));
        graphics.setStroke(new BasicStroke(1f));
      }
    }
  }
}
