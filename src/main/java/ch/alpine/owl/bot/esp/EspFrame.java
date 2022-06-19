// code by jph
package ch.alpine.owl.bot.esp;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.ascona.util.ren.AxesRender;
import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
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
