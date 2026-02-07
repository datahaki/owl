// code by jph
package ch.alpine.ascona.hull;

import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;

@ReflectionMarker
public class SymParam extends RotParam {
  @FieldSlider
  @FieldClip(min = "2", max = "10")
  public Integer layers = 5;
  @FieldSlider
  @FieldClip(min = "3", max = "10")
  public Integer n = 5;
}
