// code by astoll and jph
package ch.alpine.owl.bot.balloon;

import ch.alpine.tensor.qty.Quantity;

enum BalloonStateSpaceModels {
  ;
  static BalloonStateSpaceModel defaultWithUnits() {
    return new BalloonStateSpaceModel( //
        Quantity.of(1, "s"), //
        Quantity.of(2, "s"), //
        Quantity.of(1, "m*K^-1*s^-2"));
  }
}
