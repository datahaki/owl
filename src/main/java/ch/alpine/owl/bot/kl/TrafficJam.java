// code by jph
package ch.alpine.owl.bot.kl;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/* package */ enum TrafficJam {
  CORNERS_ONLY( //
      Tensors.vector(0, 0, 0), //
      // ---
      Tensors.vector(6, 0, 3), //
      Tensors.vector(6, 3, 0), //
      // ---
      Tensors.vector(7, 1, 4), //
      Tensors.vector(7, 3, 1)), //
  NO_CORNERS( //
      Tensors.vector(0, 0, 0), //
      // ---
      Tensors.vector(1, 3, 4), //
      Tensors.vector(1, 3, 5), //
      // ---
      Tensors.vector(2, 2, 0), //
      // ---
      Tensors.vector(3, 0, 5), //
      Tensors.vector(3, 1, 4), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 3)), //
  PROPAEDEUTIC5( //
      Tensors.vector(0, 0, 0), //
      // ---
      Tensors.vector(1, 3, 4), //
      Tensors.vector(1, 3, 5), //
      // ---
      Tensors.vector(2, 2, 0), //
      // ---
      Tensors.vector(3, 0, 5), //
      Tensors.vector(3, 1, 4), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 3), //
      // ---
      Tensors.vector(7, 1, 4), //
      Tensors.vector(7, 3, 1)),
  /** 181 */
  INSTANCE( //
      Tensors.vector(0, 0, 0), //
      // ---
      Tensors.vector(1, 3, 4), //
      Tensors.vector(1, 3, 5), //
      // ---
      Tensors.vector(2, 2, 0), //
      // ---
      Tensors.vector(3, 0, 5), //
      Tensors.vector(3, 1, 4), //
      Tensors.vector(3, 2, 2), //
      Tensors.vector(3, 2, 3), //
      Tensors.vector(3, 3, 3), //
      Tensors.vector(3, 4, 3), //
      // ---
      Tensors.vector(6, 0, 3), //
      Tensors.vector(6, 3, 0), //
      // ---
      Tensors.vector(7, 1, 4), //
      Tensors.vector(7, 3, 1)), //
  ;

  private final Tensor tensor;

  private TrafficJam(Tensor... tensor) {
    this.tensor = Tensors.of(tensor);
  }

  public KlotskiProblem create() {
    return KlotskiAdapter.create( //
        tensor, //
        "TrafficJam." + name(), //
        KlotskiStateTimeRaster.INSTANCE, //
        Tensors.vector(5, 6), //
        Tensors.vector(0, 3, 4), //
        Tensors.fromString(""), //
        Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(7, 0), //
            Tensors.vector(7, 8), //
            Tensors.vector(6, 8), //
            Tensors.vector(6, 1), //
            Tensors.vector(1, 1), //
            Tensors.vector(1, 7), //
            Tensors.vector(4, 7), //
            Tensors.vector(4, 8), //
            Tensors.vector(0, 8)).map(RealScalar.ONE.negate()::add));
  }
}
