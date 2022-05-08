// code by jph
package ch.alpine.owl.bot.delta;

import java.util.concurrent.TimeUnit;

import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.gui.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlFrame;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.AnimationWriter;
import ch.alpine.tensor.io.GifAnimationWriter;

/** simple animation of small boat driving upstream, or downstream in a river delta
 * 
 * records to animated gif */
/* package */ enum DeltaExpand1Demo {
  ;
  public static void main(String[] args) throws Exception {
    DeltaExample deltaDemo = new DeltaExample(RealScalar.of(0.5));
    OwlFrame owlFrame = OwlGui.start();
    owlFrame.addBackground(RegionRenders.create(DeltaExample.REGION));
    owlFrame.addBackground(RegionRenders.create(DeltaExample.SPHERICAL_REGION));
    // owlyFrame.addBackground(RenderElements.create(plannerConstraint));
    owlFrame.addBackground(deltaDemo.vf(0.05));
    owlFrame.geometricComponent.setOffset(33, 416);
    owlFrame.jFrame.setBounds(100, 100, 620, 475);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("delta_s.gif"), 250, TimeUnit.MILLISECONDS)) {
      GlcExpand glcExpand = new GlcExpand(deltaDemo.trajectoryPlanner);
      while (!deltaDemo.trajectoryPlanner.getBest().isPresent() && owlFrame.jFrame.isVisible()) {
        glcExpand.findAny(40);
        owlFrame.setGlc(deltaDemo.trajectoryPlanner);
        animationWriter.write(owlFrame.offscreen());
        Thread.sleep(1);
      }
      int repeatLast = 6;
      while (0 < repeatLast--)
        animationWriter.write(owlFrame.offscreen());
    }
    System.out.println("created gif");
  }
}
