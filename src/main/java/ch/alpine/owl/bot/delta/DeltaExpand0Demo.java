// code by jph
package ch.alpine.owl.bot.delta;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.tensor.RealScalar;

/** simple animation of small boat driving upstream, or downstream in a river delta
 * 
 * records to animated gif */
/* package */ enum DeltaExpand0Demo {
  ;
  static void main() throws Exception {
    // for 0.5 (in direction of river):
    // mintime w/o heuristic requires 1623 expands
    // mintime w/. heuristic requires 1334 expands
    // for -.02 (against direction of river)
    // mintime w/o heuristic requires 2846 expands
    // mintime w/. heuristic requires 2844 expands
    DeltaExample deltaDemo = new DeltaExample(RealScalar.of(-.25));
    // ---
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    GlcExpand glcExpand = new GlcExpand(deltaDemo.trajectoryPlanner);
    while (!deltaDemo.trajectoryPlanner.getBest().isPresent()) {
      glcExpand.findAny(30);
      Thread.sleep(1);
    }
    TimerFrame owlFrame = OwlGui.glc(deltaDemo.trajectoryPlanner);
    owlFrame.geometricComponent.setOffset(33, 416);
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(DeltaExample.REGION));
    owlFrame.geometricComponent.addRenderInterfaceBackground(RegionRenderFactory.create(DeltaExample.SPHERICAL_REGION));
    owlFrame.geometricComponent.addRenderInterfaceBackground(deltaDemo.vf(0.1));
    // ---
    owlFrame.jFrame.setBounds(100, 100, 620, 475);
    System.out.println("#expand = " + glcExpand.getExpandCount());
  }
}
