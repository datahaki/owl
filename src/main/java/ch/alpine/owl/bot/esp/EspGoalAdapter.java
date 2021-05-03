// code by jph
package ch.alpine.owl.bot.esp;

import java.util.List;
import java.util.Optional;

import ch.alpine.owl.glc.core.GlcNode;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;

/* package */ enum EspGoalAdapter implements GoalInterface {
  INSTANCE;

  static final Tensor GOAL = Tensors.of( //
      Tensors.vector(1, 1, 1, 0, 0), //
      Tensors.vector(1, 1, 1, 0, 0), //
      Tensors.vector(1, 1, 0, 2, 2), //
      Tensors.vector(0, 0, 2, 2, 2), //
      Tensors.vector(0, 0, 2, 2, 2), //
      Tensors.vector(2, 2) //
  ).unmodifiable();
  private static final Scalar TWO = RealScalar.of(2);

  @Override
  public Scalar minCostToGoal(Tensor x) {
    if (x instanceof Scalar)
      return DoubleScalar.POSITIVE_INFINITY;
    Tensor hi = Join.of(x.get(0).extract(0, 3), x.get(1).extract(0, 3), x.get(2).extract(0, 2));
    Tensor lo = Join.of(x.get(2).extract(3, 5), x.get(3).extract(2, 5), x.get(4).extract(2, 5));
    long hic = hi.stream().filter(scalar -> !scalar.equals(RealScalar.ONE)).count();
    long loc = lo.stream().filter(scalar -> !scalar.equals(TWO)).count();
    return RealScalar.of(Math.max(hic, loc));
  }

  @Override
  public Scalar costIncrement(GlcNode glcNode, List<StateTime> trajectory, Tensor flow) {
    return RealScalar.ONE;
  }

  @Override
  public Optional<StateTime> firstMember(List<StateTime> trajectory) {
    return trajectory.stream().filter(this::isMember).findFirst();
  }

  @Override
  public boolean isMember(StateTime element) {
    return GOAL.equals(element.state());
  }
}
