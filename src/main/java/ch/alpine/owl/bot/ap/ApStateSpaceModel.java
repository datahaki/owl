// code by astoll
package ch.alpine.owl.bot.ap;

import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.qty.Degree;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

/** State-Space Model for Flying Aircraft
 * taken from "Validating a Hamilton-Jacobi Approximation to Hybrid System Reachable Sets" by Ian Mitchel et al.
 * 
 * State space model was slightly altered as x and z are the first two entries of the vector
 * 
 * @param x = {horizontal distance (x), altitude (z), velocity, flight path angle (gamma)}
 * @param u = {thrust, angle of attack (aoa)} */
enum ApStateSpaceModel implements StateSpaceModel {
  INSTANCE;

  /** acceleration of gravity [m*s^-2] */
  private static final Scalar GRAVITY = Quantity.of(9.81, "m*s^-2");
  /** total mass of airplane [kg] */
  private static final Scalar MASS = Quantity.of(60_000, "kg");
  /** max thrust in N */
  public static final Scalar MAX_THRUST = UnitSystem.SI().apply(Quantity.of(160_000, "N"));
  /** max AOA in radian */
  public static final Scalar MAX_AOA = Degree.of(10);
  /** max speed [m*s^-1] */
  static final Scalar MAX_SPEED = Quantity.of(83, "m*s^-1");
  /** stall speed [m*s^-1] */
  static final Scalar STALL_SPEED = Quantity.of(58, "m*s^-1");
  /** steepest descent flight path angle */
  static final Scalar MAX_DESCENT_GAMMA = Degree.of(-15);
  /** maximal vertical speed in z direction during flight in [m/s] */
  static final Scalar Z_DOT_FLIGHT_MAX = MAX_SPEED.multiply(Sin.FUNCTION.apply(MAX_DESCENT_GAMMA));
  /** maximal vertical touchdown speed in z direction in [m/s] */
  static final Scalar Z_DOT_0_MAX = Quantity.of(-0.9144, "m*s^-1");
  /** altitude of final landing phase in [m] */
  static final Scalar ALTITUDE_FINAL_PHASE = Quantity.of(20, "m");

  @Override
  public Tensor f(Tensor x, Tensor u) {
    // x1' = x3*cos(x4)
    // x2' = x3*sin(x4)
    // x3' = 1/m * (u1*cos(u2) - D(u2, x3) - m*g*sin(x4))
    // x4' = 1/(m*x3) * (u1*sin(u2) + L(u2, x3) - m*g*cos(x4))
    // Scalar x1 = x.Get(0); // horizontal distance
    // Scalar x2 = x.Get(1); // altitude
    Scalar V = x.Get(2); // velocity
    Scalar x4 = x.Get(3); // flight path angle
    Scalar u1 = u.Get(0); // Thrust
    Scalar u2 = u.Get(1); // angle of attack
    return Tensors.of( //
        V.multiply(Cos.FUNCTION.apply(x4)), //
        V.multiply(Sin.FUNCTION.apply(x4)), //
        (u1.multiply(Cos.FUNCTION.apply(u2)).subtract(D(u2, V)).subtract(Times.of(MASS, GRAVITY, Sin.FUNCTION.apply(x4)))).divide(MASS), //
        (u1.multiply(Sin.FUNCTION.apply(u2)).add(L(u2, V)).subtract(Times.of(MASS, GRAVITY, Cos.FUNCTION.apply(x4)))).divide(MASS).divide(V)//
    );
  }

  @PackageTestAccess
  static Scalar D(Scalar u2, Scalar V) {
    // D(u2, x1) = (2.7 + 3.08 * (1.25 + 4.2 * u2^2) * x1^2
    double value = (1.25 + 4.2 * u2.number().doubleValue());
    return Times.of(Quantity.of(2.7 + 3.08 * value * value, "kg*m^-1"), V, V);
  }

  @PackageTestAccess
  static Scalar L(Scalar u2, Scalar V) {
    // L(u2, x1) = (68.6 * (1.25 + 4.2 * u2^2) * x1^2
    double value = (1.25 + 4.2 * u2.number().doubleValue());
    return Times.of(Quantity.of(68.6 * value, "kg*m^-1"), V, V);
  }
}
