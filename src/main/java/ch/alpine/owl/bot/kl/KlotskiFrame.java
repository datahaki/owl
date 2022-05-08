// code by jph
package ch.alpine.owl.bot.kl;

import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.tensor.Tensor;

/* package */ class KlotskiFrame extends AbstractDemo {
  private static final int RES = 128;
  // ---
  private final KlotskiPlot klotskiPlot;
  Tensor _board = null; // bad design

  public KlotskiFrame(KlotskiProblem klotskiProblem) {
    klotskiPlot = new KlotskiPlot(klotskiProblem, RES);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = _board;
    if (Objects.nonNull(board))
      klotskiPlot.new Plot(board).render(geometricLayer, graphics);
  }
}
