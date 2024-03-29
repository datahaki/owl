// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.tse2.Tse2CarEntity;
import ch.alpine.owl.bot.tse2.Tse2CarFlows;
import ch.alpine.owl.bot.tse2.Tse2ComboRegion;
import ch.alpine.owl.bot.tse2.Tse2MinTimeGoalManager;
import ch.alpine.owl.glc.adapter.CustomNodeMeritComparator;
import ch.alpine.owl.glc.adapter.LexicographicRelabelDecision;
import ch.alpine.owl.glc.adapter.VectorCostGoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.InvariantFlows;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.std.StandardTrajectoryPlanner;
import ch.alpine.owl.math.order.DiscretizedLexicographic;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.qty.Degree;

public class Tse2GokartVecEntity extends Tse2CarEntity {
  static final Tensor PARTITION_SCALE = Tensors.of( //
      RealScalar.of(2), RealScalar.of(2), Degree.of(20).reciprocal(), RealScalar.of(5)).unmodifiable();
  static final Scalar LOOKAHEAD = RealScalar.of(3.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(15);
  static final Scalar MAX_TURNING_RATE = Degree.of(23);
  public static final Tensor SHAPE = Import.of("/gokart/footprint/20171201.csv");
  static final FlowsInterface CARFLOWS = Tse2CarFlows.of(MAX_TURNING_PLAN, Tensors.vector(-0.7, 0, 0.7));
  //
  private List<CostFunction> costVector = Collections.emptyList();
  private List<Double> slackVector = Collections.emptyList();
  private Optional<Integer> timeCostPriority = Optional.empty();
  private Optional<Double> timeCostSlack = Optional.empty();

  public static Tse2GokartVecEntity createDefault(StateTime stateTime) {
    return new Tse2GokartVecEntity(stateTime, //
        CarEntity.createPurePursuitControl(), //
        PARTITION_SCALE, //
        CARFLOWS, //
        SHAPE);
  }

  public Tse2GokartVecEntity(StateTime stateTime, //
      TrajectoryControl trajectoryControl, //
      Tensor partitionScale, //
      FlowsInterface carFlows, //
      Tensor shape) {
    super(stateTime, trajectoryControl, partitionScale, carFlows, shape);
  }

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor _goal) {
    Tensor goal = Append.of(VectorQ.requireLength(_goal, 3), goalVelocity);
    goalRegion = getGoalRegionWithDistance(goal);
    Tse2ComboRegion tse2ComboRegion = Tse2ComboRegion.spherical(goal, goalRadius);
    //  ---
    // costs with higher priority come first
    List<CostFunction> costs = new ArrayList<>(costVector);
    List<Double> slacks = new ArrayList<>(slackVector);
    // ---
    if (timeCostPriority.isPresent() && timeCostSlack.isPresent()) {
      if (costs.size() < timeCostPriority.get().intValue())
        throw new RuntimeException();
      slacks.add(timeCostPriority.get(), timeCostSlack.get());
      costs.add(timeCostPriority.get(), new Tse2MinTimeGoalManager(tse2ComboRegion, controls, MAX_SPEED));
    }
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, tse2ComboRegion);
    Comparator<Tensor> comparator = DiscretizedLexicographic.of(Tensors.vector(slacks));
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), fixedStateIntegrator, //
        new InvariantFlows(controls), plannerConstraint, goalInterface, //
        new LexicographicRelabelDecision(comparator), new CustomNodeMeritComparator(comparator));
  }

  /** Sets the cost vector and their respective slacks. Lower indices have higher priority.
   * 
   * @param costVector
   * @param slackVector */
  public void setCostVector(List<CostFunction> costVector, List<Double> slackVector) {
    Integers.requireEquals(costVector.size(), slackVector.size());
    this.costVector = costVector;
    this.slackVector = slackVector;
  }

  /** Add time cost to the cost vector
   * 
   * @param priority
   * @param slack */
  public void addTimeCost(int priority, double slack) {
    this.timeCostPriority = Optional.of(priority);
    this.timeCostSlack = Optional.of(slack);
  }
}
