// code by jph
package ch.alpine.owl.bot.delta;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricComponent;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.ren.RenderElements;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.qty.Quantity;

/** simple animation of small boat driving upstream, or downstream in a river delta
 * 
 * records to animated gif */
class DeltaExpand0Demo extends AbstractDemo {
  DeltaExpand0Demo() {
    super();
    GeometricComponent geometricComponent = timerFrame.geometricComponent;
    // for 0.5 (in direction of river):
    // mintime w/o heuristic requires 1623 expands
    // mintime w/. heuristic requires 1334 expands
    // for -.02 (against direction of river)
    // mintime w/o heuristic requires 2846 expands
    // mintime w/. heuristic requires 2844 expands
    DeltaExample deltaDemo = new DeltaExample(Quantity.of(-0.25, "s^-1"));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    GlcExpand glcExpand = new GlcExpand(deltaDemo.trajectoryPlanner);
    while (!deltaDemo.trajectoryPlanner.getBest().isPresent()) {
      glcExpand.findAny(30);
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(DeltaExample.REGION));
    geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(DeltaExample.SPHERICAL_REGION));
    geometricComponent.addRenderInterfaceBackground(deltaDemo.vf(Quantity.of(0.1, "s")));
    // ---
    RenderElements.create(deltaDemo.trajectoryPlanner).forEach(geometricComponent::addRenderInterface);
    geometricComponent.setOffset(50, 680);
    geometricComponent.setPerPixel(RealScalar.of(100));
    System.out.println("#expand = " + glcExpand.getExpandCount());
  }

  static void main() {
    new DeltaExpand0Demo().runStandalone();
  }
}
