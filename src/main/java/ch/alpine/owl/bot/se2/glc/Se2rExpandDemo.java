// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.image.BufferedImage;
import java.time.Duration;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.awt.OffscreenRender;
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
        new GifAnimationWriter(HomeDirectory.Pictures.resolve("se2r.gif"), Duration.ofMillis(250))) {
      GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
      while (!trajectoryPlanner.getBest().isPresent()) {
        glcExpand.findAny(1);
        TimerFrame timerFrame = OwlGui.glc(trajectoryPlanner);
        timerFrame.setBounds(100, 100, 300, 200);
        BufferedImage bufferedImage = OffscreenRender.of(timerFrame.geometricComponent());
        animationWriter.write(bufferedImage);
        Thread.sleep(10);
      }
    }
    System.out.println("created gif");
  }
}
