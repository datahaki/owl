// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.List;

import ch.alpine.ascona.util.ren.GridRender;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Degree;

/* package */ class PursuitSimulation extends Se2Demo {
  @Override
  protected void configure(OwlAnimationFrame owlAnimationFrame) {
    List<TrajectorySample> trajectory = new ArrayList<>();
    int t = 0;
    for (Tensor angle : Subdivide.of(Degree.of(0), Degree.of(360), 100)) {
      Tensor x = AngleVector.of((Scalar) angle).multiply(RealScalar.of(2)).append(angle.add(Pi.HALF));
      StateTime stateTime = new StateTime(x, RealScalar.of(++t));
      Tensor flow = Tensors.vector(1, 0, 0);
      TrajectorySample trajectorySample = new TrajectorySample(stateTime, flow);
      trajectory.add(trajectorySample);
    }
    TrajectoryControl[] trajectoryControls = { //
        new PurePursuitControl(CarEntity.LOOKAHEAD, CarEntity.MAX_TURNING_RATE), //
        new ClothoidFixedControl(CarEntity.LOOKAHEAD, CarEntity.MAX_TURNING_RATE) };
    Tensor[] starts = { //
        Tensors.vector(2, 0, Math.PI / 2), //
        Tensors.vector(0, 2, Math.PI) };
    int index = -1;
    for (TrajectoryControl trajectoryControl : trajectoryControls) {
      CarEntity carEntity = new CarEntity( //
          new StateTime(starts[++index], RealScalar.ZERO), //
          trajectoryControl, //
          CarEntity.PARTITION_SCALE, CarEntity.CARFLOWS, CarEntity.SHAPE);
      carEntity.trajectory(trajectory);
      owlAnimationFrame.add(carEntity);
    }
    owlAnimationFrame.addBackground(new GridRender(Subdivide.of(0, 10, 5)));
    owlAnimationFrame.geometricComponent.setOffset(400, 400);
  }

  public static void main(String[] args) {
    new PursuitSimulation().start().jFrame.setVisible(true);
  }
}
