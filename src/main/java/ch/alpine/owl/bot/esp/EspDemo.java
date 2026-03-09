// code by jph
package ch.alpine.owl.bot.esp;

import java.awt.Graphics2D;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

class EspDemo extends AbstractDemo implements RenderInterface {
  static final Tensor START = Tensors.of( //
      Tensors.vector(2, 2, 2, 0, 0), //
      Tensors.vector(2, 2, 2, 0, 0), //
      Tensors.vector(2, 2, 0, 1, 1), //
      Tensors.vector(0, 0, 1, 1, 1), //
      Tensors.vector(0, 0, 1, 1, 1), //
      Tensors.vector(2, 2) //
  ).unmodifiable();

  static class Param {
    public Boolean compute = true;
  }

  private final Param param;

  public EspDemo() {
    super(param = new Param());
    geometricComponent().addRenderInterface(this);
    timerFrame.geometricComponent.setOffset(100, 600);
    timerFrame.geometricComponent.setPerPixel(RealScalar.of(100));
    fieldsEditor(0).addUniversalListener(() -> new Thread(espProvider::runStandalone).start());
  }

  private final EspProvider espProvider = new EspProvider() {
    @Override
    public boolean isContinued() {
      return timerFrame.jFrame.isVisible();
    };
  };

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = espProvider._board;
    new EspRender(board).render(geometricLayer, graphics);
  }

  static void main() {
    new EspDemo().runStandalone();
  }
}
