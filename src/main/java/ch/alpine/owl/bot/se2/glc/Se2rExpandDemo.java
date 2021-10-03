// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.concurrent.TimeUnit;

import ch.alpine.java.win.OwlFrame;
import ch.alpine.java.win.OwlGui;
import ch.alpine.owl.glc.adapter.GlcExpand;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.AnimationWriter;
import ch.alpine.tensor.io.GifAnimationWriter;

/** (x, y, theta) */
enum Se2rExpandDemo {
  ;
  public static void main(String[] args) throws Exception {
    TrajectoryPlanner trajectoryPlanner = Se2rAnimateDemo.trajectoryPlanner();
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    OwlFrame owlyFrame = OwlGui.start();
    owlyFrame.geometricComponent.setOffset(169, 71);
    owlyFrame.jFrame.setBounds(100, 100, 300, 200);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures("se2r.gif"), 250, TimeUnit.MILLISECONDS)) {
      GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
      while (!trajectoryPlanner.getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
        glcExpand.findAny(1);
        owlyFrame.setGlc(trajectoryPlanner);
        animationWriter.write(owlyFrame.offscreen());
        Thread.sleep(10);
      }
      int repeatLast = 6;
      while (0 < repeatLast--)
        animationWriter.write(owlyFrame.offscreen());
    }
    System.out.println("created gif");
  }
}
