// code by astoll
package ch.alpine.owl.bot.se2.glc;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.bot.se2.Se2MinTimeGoalManager;
import ch.alpine.owl.bot.util.FlowsInterface;
import ch.alpine.owl.glc.adapter.VectorCostGoalAdapter;
import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.glc.core.GoalInterface;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.alpine.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.alpine.owl.gui.ren.EdgeRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.RegionWithDistance;
import ch.alpine.owl.math.region.So2Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectorySample;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.qty.Degree;

public class CarRelaxedEntity extends CarEntity {
  public static CarRelaxedEntity createDefault(StateTime stateTime, Tensor slacks) {
    return new CarRelaxedEntity(stateTime, //
        createPurePursuitControl(), //
        CarEntity.PARTITION_SCALE, //
        CarEntity.CARFLOWS, //
        CarEntity.SHAPE, //
        slacks);
  }

  // ---
  private final EdgeRender edgeRender = new EdgeRender();
  private final Tensor slacks;
  private final List<CostFunction> additionalCosts = new ArrayList<>();

  private CarRelaxedEntity( //
      StateTime stateTime, //
      TrajectoryControl trajectoryControl, //
      Tensor partitionScale, //
      FlowsInterface carFlows, //
      Tensor shape, //
      Tensor slacks) {
    super(stateTime, trajectoryControl, partitionScale, carFlows, shape);
    this.slacks = slacks;
  }

  /** @param costFunction for instance, corner cutting costs */
  public void setAdditionalCostFunction(CostFunction costFunction) {
    additionalCosts.add(Objects.requireNonNull(costFunction));
  }

  @Override
  public final RelaxedTrajectoryPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    // define goal region
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    // define Se2MinTimeGoalManager
    Se2MinTimeGoalManager timeCosts = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    // set up cost vector with eventual other costs
    // construct cost vector
    List<CostFunction> costTime = Arrays.asList(timeCosts);
    List<CostFunction> costFunctionVector = Stream.concat(costTime.stream(), additionalCosts.stream()).collect(Collectors.toList());
    GoalInterface goalInterface = new VectorCostGoalAdapter(costFunctionVector, se2ComboRegion);
    // --
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster(), FIXED_STATE_INTEGRATOR, controls, plannerConstraint, goalInterface, slacks);
  }

  @Override
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new ConeRegion(goal, Degree.of(18));
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    edgeRender.setCollection(trajectoryPlanner.getQueue());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    // ---
    super.render(geometricLayer, graphics);
  }
}
