// code by jph
package ch.alpine.owl.bot.esp;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.gui.win.AbstractDemo;
import ch.alpine.tensor.Tensor;

/* package */ class EspFrame extends AbstractDemo {
  Tensor _board = null;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor board = _board;
    if (Objects.nonNull(board)) {
      new EspRender(board).render(geometricLayer, graphics);
    }
  }
}
