// code by jph
package ch.alpine.owl.bot.ip;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Objects;

import ch.alpine.ascony.ren.GridRender;
import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.PvmBuilder;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.Quantity;

/** inverted pendulum */
class IpDemo extends AbstractDemo implements RenderInterface, Runnable {
  @ReflectionMarker
  static class Param {
    @FieldFuse
    public transient Boolean compute = false;
  }

  private final IpProvider ipProvider = new IpProvider() {
    @Override
    public boolean isContinued() {
      return getWindow().isVisible();
    };
  };
  private final Param param;

  IpDemo() {
    super(param = new Param());
    fieldsEditor(param).addUniversalListener(this);
    GeometricComponent geometricComponent = geometricComponent();
    geometricComponent.addRenderInterfaceBackground(new GridRender(geometricComponent::getSize));
    Tensor digest = PvmBuilder.rhs().setOffset(50, 680) //
        .setPerPixel(Quantity.of(100, "m^-1"), Quantity.of(100, "m^-1*s")).digest();
    geometricComponent.setModel2Pixel(digest);
    geometricComponent().addRenderInterface(this);
    getWindow().addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent windowEvent) {
        run();
      }
    });
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.DARK_GRAY);
    graphics.drawString("HERE", 0, 30);
    TrajectoryPlanner trajectoryPlanner = ipProvider.trajectoryPlanner;
    if (Objects.nonNull(trajectoryPlanner)) {
      Collection<RenderInterface> collection = RenderElements.create(trajectoryPlanner);
      for (RenderInterface ri : collection)
        try {
          ri.render(geometricLayer, graphics);
        } catch (Exception e) {
        }
    }
  }

  @Override
  public void run() {
    new Thread(ipProvider::runStandalone).start();
  }

  static void main() {
    new IpDemo().runStandalone();
  }
}
