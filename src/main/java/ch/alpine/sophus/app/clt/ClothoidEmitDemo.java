// code by jph
package ch.alpine.sophus.app.clt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.BaseFrame;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidComparators;
import ch.alpine.sophus.clt.ClothoidContext;
import ch.alpine.sophus.clt.ClothoidEmit;
import ch.alpine.sophus.clt.ClothoidSolutions;
import ch.alpine.sophus.clt.ClothoidSolutions.Search;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.sca.Clips;

/** The demo shows that when using LaneRiesenfeldCurveSubdivision(Clothoid.INSTANCE, degree)
 * in order to connect two points p and q, then the (odd) degree has little influence on the
 * resulting curve. The difference is only noticeable for S shaped curves.
 * 
 * Therefore, for simplicity in algorithms we use degree == 1. */
/* package */ class ClothoidEmitDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = //
      ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ClothoidSolutions CLOTHOID_SOLUTIONS = ClothoidSolutions.of(Clips.absolute(15.0), 101);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    ClothoidContext clothoidContext = new ClothoidContext(START, mouse);
    Search search = CLOTHOID_SOLUTIONS.new Search(clothoidContext.s1(), clothoidContext.s2());
    List<Clothoid> list = ClothoidEmit.stream(clothoidContext, search.lambdas()).collect(Collectors.toList());
    int index = 0;
    for (Clothoid clothoid : list) {
      ClothoidTransition clothoidTransition = ClothoidTransition.of(START, mouse, clothoid);
      Tensor points = clothoidTransition.linearized(RealScalar.of(0.1));
      new PathRender(COLOR_DATA_INDEXED.getColor(index++), 1.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
    }
    // ---
    Optional<Clothoid> optional = list.stream().min(ClothoidComparators.CURVATURE_HEAD);
    if (optional.isPresent()) {
      Clothoid clothoid = optional.get();
      ClothoidTransition clothoidTransition = ClothoidTransition.of(START, mouse, clothoid);
      Tensor points = clothoidTransition.linearized(RealScalar.of(0.1));
      new PathRender(Color.BLACK, 2.5f) //
          .setCurve(points, false).render(geometricLayer, graphics);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidEmitDemo().setVisible(1000, 600);
  }
}
