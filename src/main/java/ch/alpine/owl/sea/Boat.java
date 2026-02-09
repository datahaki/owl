package ch.alpine.owl.sea;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.itp.LinearBinaryAverage;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.CubeRoot;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

public record Boat( //
    Scalar displacement, //
    Scalar ballast, //
    Scalar loa, //
    Scalar lwl, //
    Scalar beam, //
    Scalar sailArea) {
  // ---
  private static final Scalar MAGIC1 = Scalars.fromString("8.26[kn]");
  private static final Scalar MAGIC2 = RealScalar.of(0.311);
  private static final Scalar MAGIC3 = Scalars.fromString("64[lb]");
  private static final Scalar MAGIC4 = Scalars.fromString("2240[lb]");
  private static final Scalar MAGIC5 = Scalars.fromString("0.01[ft^-1]");
  private static final Scalar MAGIC6 = Scalars.fromString("1/0.65[ft*lb^-1]");

  public Boat( //
      String dis, //
      String sballast, //
      String sloa, String slwl, //
      String sbeam, String sA) {
    this( //
        UnitSystem.SI().apply(Scalars.fromString(dis)), //
        UnitSystem.SI().apply(Scalars.fromString(sballast)), //
        UnitSystem.SI().apply(Scalars.fromString(sloa)), //
        UnitSystem.SI().apply(Scalars.fromString(slwl)), //
        UnitSystem.SI().apply(Scalars.fromString(sbeam)), //
        UnitSystem.SI().apply(Scalars.fromString(sA)));
  }

  public Scalar hullVelocity() {
    Scalar f1 = MAGIC1.divide(Power.of(displacementLengthRatio(), MAGIC2));
    Scalar f2 = Sqrt.FUNCTION.apply(QuantityMagnitude.SI().in("ft").apply(lwl));
    return UnitConvert.SI().to("kn").apply(f1.multiply(f2));
  }

  public boolean okHullVel() {
    return Scalars.lessThan(Scalars.fromString("6[kn]"), hullVelocity());
  }

  public Scalar sailAreaDisplacementRatio() {
    Scalar den = displacementNormalized();
    return QuantityMagnitude.SI().in("ft^2").apply(sailArea).divide(den).divide(den);
  }

  public boolean okSailAreaDis() {
    return Scalars.lessThan(Scalars.fromString("12"), sailAreaDisplacementRatio());
  }

  public Scalar ballastDisplacementRatio() {
    return UnitConvert.SI().to("%").apply(ballast.divide(displacement));
  }

  public boolean okBallastDisp() {
    return Scalars.lessThan(Scalars.fromString("40[%]"), ballastDisplacementRatio());
  }

  public Scalar displacementLengthRatio() {
    Scalar temp = displacement.divide(MAGIC4).divide(Power.of(MAGIC5.multiply(lwl), 3));
    return UnitSystem.SI().apply(temp);
  }

  public boolean okDispLen() {
    return Scalars.lessThan(Scalars.fromString("250"), displacementLengthRatio());
  }

  public Scalar comfortRatio() {
    Scalar split = (Scalar) LinearBinaryAverage.INSTANCE.split(lwl, loa, RealScalar.of(0.3));
    Scalar temp = MAGIC6.multiply(displacement).divide(split).divide(Power.of(beamFt(), 1.33));
    return UnitSystem.SI().apply(temp);
  }

  public boolean okComfort() {
    return Scalars.lessThan(Scalars.fromString("35"), comfortRatio());
  }

  public Scalar capsize() {
    return beamFt().divide(displacementNormalized());
  }

  public boolean okCapsize() {
    return Scalars.lessThan(capsize(), Scalars.fromString("2"));
  }

  private Scalar displacementNormalized() {
    return CubeRoot.FUNCTION.apply(UnitSystem.SI().apply(displacement.divide(MAGIC3)));
  }

  private Scalar beamFt() {
    return QuantityMagnitude.SI().in("ft").apply(beam);
  }

  public String textValues() {
    return textValues(Round._2);
  }

  public String textValues(ScalarUnaryOperator suo) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("hullVel\t" + hullVelocity().maps(suo) + "\t" + okHullVel() + "\n");
    stringBuilder.append("sailArD\t" + sailAreaDisplacementRatio().maps(suo) + "\t" + okSailAreaDis() + "\n");
    stringBuilder.append("balastD\t" + ballastDisplacementRatio().maps(suo) + "\t" + okBallastDisp() + "\n");
    stringBuilder.append("displLn\t" + displacementLengthRatio().maps(suo) + "\t" + okDispLen() + "\n");
    stringBuilder.append("comfort\t" + comfortRatio().maps(suo) + "\t" + okComfort() + "\n");
    stringBuilder.append("capsize\t" + capsize().maps(suo) + "\t" + okCapsize() + "\n");
    return stringBuilder.toString();
  }
}
