// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.concurrent.TimeUnit;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.io.AnimationWriter;
import ch.alpine.bridge.io.GifAnimationWriter;
import ch.alpine.owl.util.win.OwlGui;
import ch.alpine.owlets.glc.adapter.GlcExpand;
import ch.alpine.owlets.glc.core.TrajectoryPlanner;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.HomeDirectory;

/** (x, y, theta) */
enum Se2rExpandDemo {
  ;
  static void main() throws Exception {
    TrajectoryPlanner trajectoryPlanner = Se2rAnimateDemo.trajectoryPlanner();
    // ---
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(3), RealScalar.ZERO));
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures.resolve("se2r.gif"), 250, TimeUnit.MILLISECONDS)) {
      GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
      while (!trajectoryPlanner.getBest().isPresent()) {
        glcExpand.findAny(1);
        TimerFrame owlFrame = OwlGui.glc(trajectoryPlanner);
        // owlFrame.geometricComponent.setOffset(169, 71);
        owlFrame.jFrame.setBounds(100, 100, 300, 200);
        animationWriter.write(owlFrame.offscreen());
        Thread.sleep(10);
      }
    }
    System.out.println("created gif");
  }
}
