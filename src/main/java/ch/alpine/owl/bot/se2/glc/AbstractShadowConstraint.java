// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiFunction;

import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.bot.tse2.Tse2StateSpaceModel;
import ch.alpine.owl.data.tree.Nodes;
import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Lists;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.lie.r2.AngleVector;

abstract class AbstractShadowConstraint implements PlannerConstraint, Serializable {
  static final Tensor DIR = AngleVector.of(RealScalar.ZERO).unmodifiable();
  private static final int RESOLUTION = 10;
  /** values for seglength have been 3 and 4 */
  private static final int SEGLENGTH = 3;
  private static final float TIMESTEP = 0.1f; // TODO_YN get from state integrator
  // ---
  private final int steps;
  private final BiFunction<StateTime, Tensor, Scalar> velSupplier;
  // ---
  final float a;
  final float tReact;

  public AbstractShadowConstraint(float a, float tReact, boolean tse2) {
    // TODO_YN hack
    // GlobalAssert.that((tReact / TIMESTEP) % SEGLENGTH == 0); // <- does not work
    this.a = a;
    this.tReact = tReact;
    this.steps = Math.max((int) Math.ceil(tReact / TIMESTEP), 1);
    velSupplier = tse2 //
        ? (StateTime stateTime, Tensor flow) -> stateTime.state().Get(Tse2StateSpaceModel.STATE_INDEX_VEL)
        : (StateTime stateTime, Tensor flow) -> flow.Get(Se2StateSpaceModel.CONTROL_INDEX_VEL);
  }

  @Override
  public final boolean isSatisfied(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    // TODO_YN there are few different values for vel => precompute
    StateTime childStateTime = Lists.last(trajectory);
    float vel = velSupplier.apply(childStateTime, flow).number().floatValue();
    float tBrake = vel / a;
    float dBrake = tBrake * vel / 2;
    Tensor range = Subdivide.of(0.0, dBrake, RESOLUTION);
    Tensor ray = TensorProduct.of(range, DIR);
    Se2Bijection se2Bijection = new Se2Bijection(childStateTime.state());
    // -
    StateTime pastStateTime;
    if (steps <= SEGLENGTH)
      pastStateTime = trajectory.get(trajectory.size() - steps);
    else {
      int xx = steps / SEGLENGTH;
      GlcNode prevnode = Nodes.getParent(glcNode, xx);
      pastStateTime = prevnode.stateTime();
    }
    return isSatisfied(pastStateTime, tBrake, ray, se2Bijection.forward());
  }

  abstract boolean isSatisfied(final StateTime stateTime, float tBrake, Tensor ray, TensorUnaryOperator forward);
}
