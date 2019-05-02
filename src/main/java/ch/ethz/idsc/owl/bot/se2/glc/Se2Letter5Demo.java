// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.math.planar.InterpolationEntryFinder;

public class Se2Letter5Demo extends Se2LetterADemo {
  @Override
  public TrajectoryControl createTrajectoryControl() {
    return new ClothoidPursuitControl(new InterpolationEntryFinder(0), CarEntity.MAX_TURNING_RATE);
  }

  public static void main(String[] args) {
    new Se2Letter5Demo().start().jFrame.setVisible(true);
  }
}
