// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.BaseFrame;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.crv.dubins.DubinsPath;
import ch.alpine.sophus.crv.dubins.DubinsPathGenerator;
import ch.alpine.sophus.crv.dubins.FixedRadiusDubins;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

public class DubinsTransitionDemo extends AbstractDemo implements DemoInterface {
  private static final Tensor START = Array.zeros(3).unmodifiable();
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();

  public DubinsTransitionDemo() {
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor mouse = timerFrame.geometricComponent.getMouseSe2CState();
    // ---
    DubinsPathGenerator dubinsPathGenerator = FixedRadiusDubins.of(START, mouse, RealScalar.of(1));
    List<DubinsPath> list = dubinsPathGenerator.stream().collect(Collectors.toList());
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(5));
    {
      graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
      graphics.setStroke(new BasicStroke(1));
      for (DubinsPath dubinsPath : list) {
        DubinsTransition dubinsTransition = new DubinsTransition(START, mouse, dubinsPath);
        graphics.draw(geometricLayer.toPath2D(dubinsTransition.linearized(minResolution)));
      }
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    new DubinsTransitionDemo().setVisible(1000, 600);
  }
}
