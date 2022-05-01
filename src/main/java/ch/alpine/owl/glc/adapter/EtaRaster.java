// code by jph
package ch.alpine.owl.glc.adapter;

import java.io.Serializable;
import java.util.function.Function;

import ch.alpine.owl.glc.core.StateTimeRaster;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Floor;

/** mapping from state time to domain coordinates according to the standard projection
 * Floor(eta .* represent(state))
 * 
 * <p>Examples for represent are:
 * identity for R^n
 * mod for So2
 * log for Lotka-Volterra state space model
 * 
 * <p>The default value drops time information and only considers
 * {@link StateTime#state()}. */
public class EtaRaster implements StateTimeRaster, Serializable {
  /** @param eta
   * @return uniform raster for {@link StateTime#state()} */
  @SuppressWarnings("unchecked")
  public static StateTimeRaster state(Tensor eta) {
    return new EtaRaster(eta, (Function<StateTime, Tensor> & Serializable) StateTime::state);
  }

  /** @param eta
   * @return uniform raster for {@link StateTime#joined()} */
  @SuppressWarnings("unchecked")
  public static StateTimeRaster joined(Tensor eta) {
    return new EtaRaster(eta, (Function<StateTime, Tensor> & Serializable) StateTime::joined);
  }

  /** @param eta
   * @param dt in exact precision
   * @param represent for instance StateTime::joined
   * @return
   * @throws Exception if dt is not in exact precision */
  public static StateTimeRaster timeDependent(Tensor eta, Scalar dt, Function<StateTime, Tensor> represent) {
    return new EtaRaster(Append.of(eta, ExactScalarQ.require(dt).reciprocal()), represent);
  }

  // ---
  private final Tensor eta;
  private final Function<StateTime, Tensor> represent;

  public EtaRaster(Tensor eta, Function<StateTime, Tensor> represent) {
    this.eta = eta.copy();
    this.represent = represent;
  }

  @Override // from StateTimeRasterization
  public Tensor convertToKey(StateTime stateTime) {
    return Times.of(eta, represent.apply(stateTime)).map(Floor.FUNCTION);
  }

  /** @return unmodifiable */
  public Tensor eta() {
    return eta.unmodifiable();
  }
}
