// code by jph, gjoel
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.math.pursuit.InterpolationEntryFinder;

public class Se2Letter5Demo extends Se2LetterADemo {
  @Override
  public TrajectoryControl createTrajectoryControl() {
    return new ClothoidPursuitControl(InterpolationEntryFinder.INSTANCE, CarEntity.MAX_TURNING_RATE);
  }

  public static void main(String[] args) {
    new Se2Letter5Demo().start().jFrame.setVisible(true);
  }
}
