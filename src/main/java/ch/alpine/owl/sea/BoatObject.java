// code by jph
package ch.alpine.owl.sea;

import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.tensor.Scalar;

public class BoatObject {
  @FieldSlider(showValue = true)
  @FieldClip(min = "2000.0[kg]", max = "20000.0[kg]")
  public Scalar displacement;
  @FieldSlider(showValue = true)
  @FieldClip(min = "1000.0[kg]", max = "10000.0[kg]")
  public Scalar ballast;
  @FieldSlider(showValue = true)
  @FieldClip(min = "5.0[m]", max = "20[m]")
  public Scalar loa;
  @FieldSlider(showValue = true)
  @FieldClip(min = "5.0[m]", max = "20[m]")
  public Scalar lwl;
  @FieldSlider(showValue = true)
  @FieldClip(min = "2.0[m]", max = "5[m]")
  public Scalar beam;
  @FieldSlider(showValue = true)
  @FieldClip(min = "5.0[m^2]", max = "100[m^2]")
  public Scalar sailArea;

  public BoatObject(Boat boat) {
    displacement = boat.displacement();
    ballast = boat.ballast();
    loa = boat.loa();
    lwl = boat.lwl();
    beam = boat.beam();
    sailArea = boat.sailArea();
  }

  public Boat create() {
    return new Boat(displacement, ballast, loa, lwl, beam, sailArea);
  }
}
