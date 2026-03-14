// code by jph
package ch.alpine.owl.bot.esp;

import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.Tensor;

class EspDemo extends AbstractDemo implements RenderInterface, Runnable {
  @ReflectionMarker
  static class Param {
    @FieldFuse
    public transient Boolean compute = false;
  }

  private final EspProvider espProvider = new EspProvider() {
    @Override
    public boolean isContinued() {
      return timerFrame.isVisible();
    };
  };
  @SuppressWarnings("unused")
  private final Param param;

  public EspDemo() {
    super(param = new Param());
    fieldsEditor(0).addUniversalListener(this);
    geometricComponent().addRenderInterface(this);
    Tensor pvm = PvmBuilder.rhs().setOffset(100, 600).setPerPixel(100).digest();
    geometricComponent().setModel2Pixel(pvm);
    timerFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent windowEvent) {
        run();
      }
    });
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor board = espProvider._board;
    new EspRender(board).render(geometricLayer, graphics);
  }

  @Override
  public void run() {
    new Thread(espProvider::runStandalone).start();
  }

  static void main() {
    new EspDemo().runStandalone();
  }
}
