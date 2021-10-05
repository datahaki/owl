// code by jph
package ch.alpine.sophus.demo.lev;

import java.util.Objects;

import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.Biinvariants;

public enum Bitype {
  METRIC1, //
  METRIC2, //
  LEVERAGES1, //
  LEVERAGES2, //
  GARDEN, //
  HARBOR, //
  CUPOLA, //
  ;

  public Biinvariant from(ManifoldDisplay manifoldDisplay) {
    switch (this) {
    case METRIC1:
    case METRIC2:
      Biinvariant biinvariant = manifoldDisplay.metricBiinvariant();
      return Objects.nonNull(biinvariant) //
          ? biinvariant
          : Biinvariants.LEVERAGES;
    case LEVERAGES1:
    case LEVERAGES2:
      return Biinvariants.LEVERAGES;
    case GARDEN:
      return Biinvariants.GARDEN;
    case HARBOR:
      return Biinvariants.HARBOR;
    case CUPOLA:
      return Biinvariants.CUPOLA;
    default:
      throw new IllegalArgumentException();
    }
  }
}
