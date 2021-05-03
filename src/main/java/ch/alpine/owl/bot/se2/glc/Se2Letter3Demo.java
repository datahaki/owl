// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.api.TrajectoryControl;

public class Se2Letter3Demo extends Se2LetterADemo {
  @Override // from Se2LetterADemo
  public TrajectoryControl createTrajectoryControl() {
    return CarEntity.createPurePursuitControl();
  }

  public static void main(String[] args) {
    new Se2Letter3Demo().start().jFrame.setVisible(true);
  }
}
