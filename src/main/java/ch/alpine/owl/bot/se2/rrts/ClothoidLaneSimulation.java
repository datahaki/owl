// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.dis.Se2ClothoidDisplay;
import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.SimpleLaneConsumer;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.data.tree.Nodes;
import ch.alpine.owl.lane.LaneConsumer;
import ch.alpine.owl.lane.LaneInterface;
import ch.alpine.owl.lane.StableLanes;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.sophis.ts.ClothoidTransitionSpace;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.ScalarSummaryStatistics;
import ch.alpine.tensor.red.StandardDeviation;
import ch.alpine.tensor.sca.Clips;

/* package */ enum ClothoidLaneSimulation {
  ;
  private static final Tensor[] CONTROLS = { //
      Tensors.fromString("{{6.017, 4.983, 0.785},{8.100, 5.100, -1.571},{1.667, 1.950, -3.142}}"), //
      Tensors.fromString("{{1.817, 7.283, -3.665},{5.483, 8.817, -7.854},{7.950, 10.733, -3.142}}"), //
      Tensors.fromString("{{6.000, 5.617, -1.571},{6.967, 2.500, 0.000},{9.350, 2.500, 0.000},{10.383, 5.500, 1.571}}"), //
      Tensors.fromString("{{1.000, 4.450, 0.000},{5.500, 4.450, 0.000},{6.350, 5.650, 0.000},{10.500, 5.650, 0.000}}"), //
      Tensors.fromString("{{8.117, 3.300, 1.571},{6.967, 6.717, 2.618},{4.150, 7.167, 3.142},{3.383, 8.600, 1.571}}"), //
      Tensors.fromString("{{5.750, 1.917, -3.142},{2.000, 1.917, -3.142},{1.150, 3.250, -4.712},{2.000, 4.450, -6.283},"
          + "{5.000, 4.450, -6.283},{8.050, 6.500, -6.283},{7.767, 10.750, -3.142}}") };
  private static final int REPS = 25;
  private static final Scalar DELAY_HINT = RealScalar.of(3);
  // ---
  private static final Path DIRECTORY = HomeDirectory.Pictures.resolve("LaneSim");
  private static final int WIDTH = 900;
  private static final int HEIGHT = 600;
  private static final Scalar OVERHEAD = RealScalar.of(0.5);
  private static final Scalar MIN_RESOLUTION = RealScalar.of(0.1);
  // ---
  private static final ManifoldDisplay MANIFOLD_DISPLAY = Se2ClothoidDisplay.ANALYTIC;
  private static final int DEGREE = 3;
  private static final int LEVELS = 5;
  private static final Scalar LANE_WIDTH = RealScalar.of(1.1);
  // ---
  private static final R2ImageRegionWrap R2_IMAGE_REGION_WRAP = R2ImageRegions._GTOB;
  private static final TransitionRegionQuery TRANSITION_REGION_QUERY = TransitionRegionQueryUnion.wrap( //
      new SampledTransitionRegionQuery(R2_IMAGE_REGION_WRAP.region(), RealScalar.of(0.05)), //
      new ClothoidCurvatureQuery(Clips.absolute(5.)));

  static void main() throws Exception {
    Files.createDirectories(DIRECTORY);
    try (PrintWriter writer = new PrintWriter(DIRECTORY.resolve("report.txt").toFile())) {
      int task = 1;
      for (Tensor controlPoints : CONTROLS) {
        // controlPoints.stream().forEach(System.out::println);
        LaneInterface lane = StableLanes.of( //
            controlPoints, //
            LaneRiesenfeldCurveSubdivision.of(MANIFOLD_DISPLAY.geodesicSpace(), DEGREE)::string, //
            LEVELS, LANE_WIDTH.multiply(Rational.HALF));
        // ---
        Tensor diagonal = Tensors.of( //
            RealScalar.of(WIDTH /* graphics.getWidth() */).divide(R2_IMAGE_REGION_WRAP.range().Get(0)), //
            RealScalar.of(WIDTH /* graphics.getHeight() */).divide(R2_IMAGE_REGION_WRAP.range().Get(1)), //
            RealScalar.ONE);
        Tensor matrix = DiagonalMatrix.sparse(diagonal);
        GeometricLayer geometricLayer = new GeometricLayer(matrix);
        // SVGUtils.writeToSVG(new File(DIRECTORY, String.format("scenario_%d.svg", task)),
        // scenario(geometricLayer, lane).getSVGElement() /* graphics.getSVGElement() */);
        // ---
        Tensor ttfs = Tensors.empty();
        Show show = new Show();
        // show.getAxisX().setLabel("time [s]");
        // show.getAxisY().setLabel("cost");
        for (int rep = 0; rep < REPS; rep++) {
          System.out.println("iteration " + (rep + 1));
          run(lane, show, ttfs, geometricLayer, task, rep + 1);
        }
        ScalarSummaryStatistics statistics = new ScalarSummaryStatistics();
        ttfs.stream().map(Scalar.class::cast).forEach(statistics);
        String summary = String.format("scenario %d:" //
            + "\n\ttime to first solution = %s +/- %s (min=%s, max=%s)" + "\n\tsuccess rate: %.2f%%", //
            task, statistics.getAverage(), StandardDeviation.ofVector(ttfs), statistics.getMin(), statistics.getMax(), 100. * ttfs.length() / REPS);
        writer.println(summary.replace("\n", System.lineSeparator()));
        System.out.println("\n" + summary + "\n");
        // Showable jFreeChart = ListPlot.of(show);
        // TODO OWL can simplify!
        // Plot plot = jFreeChart.getPlot();
        // XYPlot xyPlot = (XYPlot) plot;
        // xyPlot.setDomainAxis(new LogarithmicAxis(visualSet.getAxisX().getLabel()));
        // List<CoordinateBoundingBox> minMaxes = show.visualRows().stream().map(VisualRow::points).filter(Tensors::nonEmpty) //
        // .map(points -> CoordinateBounds.of(points.get(Tensor.ALL, 1))).collect(Collectors.toList());
        // xyPlot.getRangeAxis().setRange( //
        // Math.max(0., 0.9 * minMaxes.stream() //
        // .map(CoordinateBoundingBox::min) //
        // .map(Scalar.class::cast) //
        // .reduce(Min::of) //
        // .get().number().doubleValue()), //
        // 1.1 * minMaxes.stream() //
        // .map(CoordinateBoundingBox::max) //
        // .map(Scalar.class::cast) //
        // .reduce(Max::of) //
        // .get().number().doubleValue());
        Path file = DIRECTORY.resolve(String.format("costs_%d.png", task++));
        show.export(file, new Dimension(WIDTH, HEIGHT));
      }
    }
  }

  private synchronized static void run(LaneInterface lane, Show visualSet, Tensor ttfs, GeometricLayer geometricLayer, int task, int rep) throws Exception {
    StateTime stateTime = new StateTime(lane.midLane().get(0), RealScalar.ZERO);
    Consumer<Map<Scalar, Scalar>> process = observations -> {
      Tensor domain = Tensor.of(observations.keySet().stream().map(d -> Quantity.of(d, "s")));
      Tensor values = Tensor.of(observations.values().stream());
      visualSet.add(ListPlot.of(domain, values));
      observations.keySet().stream().findFirst().ifPresent(ttfs::append);
    };
    List<RrtsNode> first = new ArrayList<>();
    List<RrtsNode> last = new ArrayList<>();
    ClothoidLaneEntity entity = //
        new ClothoidLaneEntity(stateTime, TRANSITION_REGION_QUERY, R2_IMAGE_REGION_WRAP.coordinateBounds(), true, DELAY_HINT, //
            process, rrtsNode -> first.addAll(Nodes.listFromRoot(rrtsNode)), rrtsNode -> last.addAll(Nodes.listFromRoot(rrtsNode)));
    LaneConsumer laneConsumer = new SimpleLaneConsumer(entity, null, Collections.singleton(entity));
    laneConsumer.accept(lane);
    Thread.sleep((long) (DELAY_HINT.add(OVERHEAD).number().doubleValue() * 1000));
    // ---
    // if (!last.isEmpty()) {
    // SVGGraphics2D graphics = scenario(geometricLayer, lane);
    // // TreeRender treeRender = new TreeRender();
    // // treeRender.setCollection(Nodes.ofSubtree(last.get(0)));
    // // treeRender.render(geometricLayer, graphics);
    // TransitionRender transitionRender = new TransitionRender(ClothoidTransitionSpace.ANALYTIC);
    // transitionRender.setCollection(Nodes.ofSubtree(last.get(0)));
    // transitionRender.render(geometricLayer, graphics);
    // render(first, geometricLayer, graphics, Color.ORANGE);
    // render(last, geometricLayer, graphics, Color.BLUE);
    // SVGUtils.writeToSVG(new File(DIRECTORY, String.format("scenario_%d_%d.svg", task, rep)), graphics.getSVGElement());
    // }
  }

  private static void render(Collection<RrtsNode> nodes, GeometricLayer geometricLayer, Graphics2D graphics2D, Color color) {
    Tensor points = Tensors.empty();
    Iterator<RrtsNode> iterator = nodes.iterator();
    for (RrtsNode end = iterator.next(); iterator.hasNext();) {
      RrtsNode start = end;
      end = iterator.next();
      ClothoidTransitionSpace.ANALYTIC.connect(start.state(), end.state()).linearized(MIN_RESOLUTION).forEach(points::append);
    }
    graphics2D.setColor(color);
    graphics2D.draw(geometricLayer.toPath2D(points));
  }
  //
  // private static SVGGraphics2D scenario(GeometricLayer geometricLayer, LaneInterface lane) {
  // SVGGraphics2D graphics = new SVGGraphics2D(WIDTH, WIDTH);
  // graphics.setColor(Color.WHITE);
  // graphics.fillRect(0, 0, graphics.getWidth(), graphics.getHeight());
  // graphics.setColor(new Color(0, 0, 0, 16));
  // RegionRenders.create(R2_IMAGE_REGION_WRAP.region()).render(geometricLayer, graphics);
  // LaneRender laneRender = new LaneRender();
  // laneRender.setLane(lane, false);
  // laneRender.render(geometricLayer, graphics);
  // PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  // pointsRender.show(GEODESIC_DISPLAY::matrixLift, GEODESIC_DISPLAY.shape(), lane.controlPoints()).render(geometricLayer, graphics);
  // return graphics;
  // }
}
