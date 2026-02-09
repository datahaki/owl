// code by jph
package ch.alpine.owl.bot.se2;

import ch.alpine.owl.bot.se2.glc.Se2CarFlows;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.flow.RungeKutta4Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Timing;

enum Se2IntegratorDemo {
  ;
  /** Example output:
   * car_int 0.033889832
   * se2_int 0.10052994000000001
   * runge4_ 0.12139695800000001
   * runge45 0.407662399 */
  static void main() {
    StateSpaceModel stateSpaceModel = Se2StateSpaceModel.INSTANCE;
    Tensor flow = Se2CarFlows.singleton(RealScalar.ONE, RealScalar.ONE);
    Timing s1 = Timing.stopped();
    Timing s4 = Timing.stopped();
    Timing s2 = Timing.stopped();
    Timing s3 = Timing.stopped();
    for (int count = 0; count < 10_000; ++count) {
      Tensor x = RandomVariate.of(NormalDistribution.standard(), 3);
      Scalar h = RandomVariate.of(NormalDistribution.standard());
      s1.start();
      Se2CarIntegrator.INSTANCE.step(stateSpaceModel, x, flow, h);
      s1.stop();
      s4.start();
      Se2FlowIntegrator.INSTANCE.step(stateSpaceModel, x, flow, h);
      s4.stop();
      s2.start();
      RungeKutta4Integrator.INSTANCE.step(stateSpaceModel, x, flow, h);
      s2.stop();
      s3.start();
      RungeKutta45Integrator.INSTANCE.step(stateSpaceModel, x, flow, h);
      s3.stop();
    }
    System.out.println("car_int " + s1.seconds());
    System.out.println("se2_int " + s4.seconds());
    System.out.println("runge4_ " + s2.seconds());
    System.out.println("runge45 " + s3.seconds());
  }
}
