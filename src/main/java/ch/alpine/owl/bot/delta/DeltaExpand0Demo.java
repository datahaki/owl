// code by jph
package ch.alpine.owl.bot.delta;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.tensor.RealScalar;

/** simple animation of small boat driving upstream, or downstream in a river delta
 * 
 * records to animated gif */
/* package */ enum DeltaExpand0Demo {
  ;
  public static void main(String[] args) throws Exception {
    // for 0.5 (in direction of river):
    // mintime w/o heuristic requires 1623 expands
    // mintime w/. heuristic requires 1334 expands
    // for -.02 (against direction of river)
    // mintime w/o heuristic requires 2846 expands
    // mintime w/. heuristic requires 2844 expands
    DeltaExample deltaDemo = new DeltaExample(RealScalar.of(-.25));
    // ---
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.geometricComponent.setOffset(33, 416);
    owlFrame.addBackground(RegionRenders.create(DeltaExample.REGION));
    owlFrame.addBackground(RegionRenders.create(DeltaExample.SPHERICAL_REGION));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    // owlyFrame.addBackground(new DomainRender(trajectoryPlanner.getDomainMap(), eta));
    // ---
    owlFrame.addBackground(deltaDemo.vf(0.1));
    // ---
    owlFrame.jFrame.setBounds(100, 100, 620, 475);
    GlcExpand glcExpand = new GlcExpand(deltaDemo.trajectoryPlanner);
    while (!deltaDemo.trajectoryPlanner.getBest().isPresent() && owlFrame.jFrame.isVisible()) {
      glcExpand.findAny(30);
      owlFrame.setGlc(deltaDemo.trajectoryPlanner);
      Thread.sleep(1);
    }
    System.out.println("#expand = " + glcExpand.getExpandCount());
  }
}
