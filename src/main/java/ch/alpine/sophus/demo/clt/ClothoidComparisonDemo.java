// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.java.win.BaseFrame;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransition;
import ch.alpine.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.LagrangeQuadraticD;
import ch.alpine.sophus.ext.api.CurveVisualSet;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.Se2CoveringClothoidDisplay;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

/** demo compares conventional clothoid approximation with extended winding
 * number clothoid approximation to generate figures in report:
 * 
 * https://github.com/idsc-frazzoli/retina/files/3568308/20190903_appox_clothoids_with_ext_windings.pdf */
/* package */ class ClothoidComparisonDemo extends AbstractDemo implements DemoInterface {
  private static final int WIDTH = 480;
  private static final int HEIGHT = 360;
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    // ---
    ManifoldDisplay manifoldDisplay = Se2CoveringClothoidDisplay.INSTANCE;
    {
      Tensor shape = manifoldDisplay.shape();
      geometricLayer.pushMatrix(GfxMatrix.of(Array.zeros(3)));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(new Color(255, 0, 0, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(255, 0, 0, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
      // ---
      geometricLayer.pushMatrix(GfxMatrix.of(mouse));
      graphics.draw(geometricLayer.toPath2D(shape, true));
      geometricLayer.popMatrix();
    }
    VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
    for (ClothoidTransitionSpace clothoidTransitionSpace : ClothoidTransitionSpace.values()) {
      int ordinal = clothoidTransitionSpace.ordinal();
      Color color = COLOR_DATA_INDEXED.getColor(ordinal);
      {
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        graphics.setColor(color);
        graphics.drawString(clothoidTransitionSpace.name(), 0, 24 + ordinal * 14);
      }
      ClothoidTransition clothoidTransition = clothoidTransitionSpace.connect(START, mouse);
      Clothoid clothoid = clothoidTransition.clothoid();
      Tensor points = clothoidTransition.linearized(RealScalar.of(geometricLayer.pixel2modelWidth(5)));
      new PathRender(color, 1.5f).setCurve(points, false).render(geometricLayer, graphics);
      // ---
      Tensor tensor = Tensor.of(points.stream().map(manifoldDisplay::toPoint));
      CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
      curveVisualSet.addCurvature(visualSet);
      {
        LagrangeQuadraticD curvature = clothoid.curvature();
        Tensor domain = curveVisualSet.getArcLength1();
        visualSet.add(domain, ConstantArray.of(curvature.head(), domain.length()));
        visualSet.add(domain, ConstantArray.of(curvature.tail(), domain.length()));
        visualSet.add(domain, Subdivide.of(0.0, 1.0, domain.length() - 1).map(curvature));
        visualSet.add(domain, Subdivide.of(0.0, 1.0, domain.length() - 1).map(clothoid::addAngle));
      }
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet, true);
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new ClothoidComparisonDemo().setVisible(1000, 390);
  }
}
