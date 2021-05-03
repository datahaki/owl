// code by ynager
package ch.alpine.owl.bot.se2.glc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
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
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

/** gokart that operates on vector costs */
public class GokartVecEntity extends GokartEntity {
  public GokartVecEntity(StateTime stateTime) {
    super(stateTime);
  }

  private List<CostFunction> costVector = new ArrayList<>();
  private List<Double> slackVector = new ArrayList<>();
  // ---
  private Optional<Integer> timeCostPriority = Optional.empty();
  private Optional<Double> timeCostSlack = Optional.empty();

  @Override
  public final TrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    //  ---
    // costs with higher priority come first
    List<CostFunction> costs = new ArrayList<>();
    List<Double> slacks = new ArrayList<>();
    costs.addAll(costVector);
    slacks.addAll(slackVector);
    // ---
    if (timeCostPriority.isPresent() && timeCostSlack.isPresent()) {
      slacks.add(timeCostPriority.get(), timeCostSlack.get());
      costs.add(timeCostPriority.get(), new Se2MinTimeGoalManager(se2ComboRegion, controls));
    }
    // ---
    GoalInterface goalInterface = new VectorCostGoalAdapter(costs, se2ComboRegion);
    Comparator<Tensor> comparator = DiscretizedLexicographic.of(Tensors.vector(slacks));
    return new StandardTrajectoryPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, //
        new InvariantFlows(controls), plannerConstraint, goalInterface, //
        new LexicographicRelabelDecision(comparator), //
        new CustomNodeMeritComparator(comparator));
  }

  /** Sets the cost vector and their respective slacks. Lower indices have higher priority.
   * @param costVector
   * @param slackVector */
  public void setCostVector(List<CostFunction> costVector, List<Double> slackVector) {
    if (costVector.size() != slackVector.size())
      throw new RuntimeException();
    this.costVector = costVector;
    this.slackVector = slackVector;
  }

  /** Add time cost to the cost vector
   * @param priority
   * @param slack */
  public void addTimeCost(int priority, Double slack) {
    this.timeCostPriority = Optional.of(priority);
    this.timeCostSlack = Optional.of(slack);
  }
}
