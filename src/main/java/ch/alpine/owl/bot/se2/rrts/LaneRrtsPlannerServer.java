// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ch.alpine.owl.lane.LaneConsumer;
import ch.alpine.owl.lane.LaneInterface;
import ch.alpine.owl.lane.LaneRandomSample;
import ch.alpine.owl.lane.Se2ConeRandomSample;
import ch.alpine.owl.lane.Se2SphereRandomSample;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.rrts.DefaultRrtsPlannerServer;
import ch.alpine.owl.rrts.core.TransitionCostFunction;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.Region;
import ch.alpine.sophus.math.sample.ConstantRandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.qty.Degree;

// TODO don't use magic constants at all. implement via interface, make interface final 
public abstract class LaneRrtsPlannerServer extends DefaultRrtsPlannerServer implements LaneConsumer {
  private static final Distribution DEFAULT_ROT_DIST = NormalDistribution.of(RealScalar.ZERO, Degree.of(5));
  // ---
  private final boolean greedy;
  private RandomSampleInterface laneSampler;
  private RandomSampleInterface goalSampler;
  private Region<Tensor> goalRegion;
  // ---
  private boolean conical = false;
  private Scalar mu_r = RealScalar.of(3);
  private Scalar semi = Degree.of(17);
  private Scalar heading = Degree.of(5);
  private Distribution rotDist = DEFAULT_ROT_DIST;

  public LaneRrtsPlannerServer( //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery obstacleQuery, //
      Scalar resolution, //
      StateSpaceModel stateSpaceModel, //
      TransitionCostFunction costFunction, //
      boolean greedy) {
    super(transitionSpace, obstacleQuery, resolution, stateSpaceModel, costFunction);
    this.greedy = greedy;
  }

  @Override // from DefaultRrtsPlannerServer
  protected final RandomSampleInterface spaceSampler(Tensor state) {
    // TODO document why laneSampler might not be "ready" to be returned
    return Objects.nonNull(laneSampler) //
        ? laneSampler
        : new ConstantRandomSample(state);
  }

  @Override // from DefaultRrtsPlannerServer
  protected final RandomSampleInterface goalSampler(Tensor state) {
    return Objects.nonNull(goalSampler) //
        ? goalSampler
        : new ConstantRandomSample(state);
  }

  @Override // from Consumer
  public final void accept(LaneInterface laneInterface) {
    laneSampler = LaneRandomSample.of(laneInterface, rotDist);
    final Tensor apex = Last.of(laneInterface.midLane());
    if (conical) {
      goalSampler = Se2ConeRandomSample.of(apex, semi, heading, mu_r);
      goalRegion = new ConeRegion(apex, semi);
    } else {
      Scalar radius = Last.of(laneInterface.margins());
      goalSampler = new Se2SphereRandomSample(apex, radius, rotDist);
      goalRegion = new BallRegion(Extract2D.FUNCTION.apply(apex), radius);
    }
    if (greedy)
      setGreeds(laneInterface.controlPoints().stream().collect(Collectors.toList()));
  }

  public final Optional<Region<Tensor>> goalRegion() {
    return Optional.ofNullable(goalRegion);
  }

  public final void setConical(boolean conical) {
    this.conical = conical;
  }

  public final void setRotationDistribution(Distribution distribution) {
    rotDist = distribution;
  }

  public final void setCone(Scalar mu_r, Scalar semi) {
    this.mu_r = mu_r;
    this.semi = semi;
  }
}
