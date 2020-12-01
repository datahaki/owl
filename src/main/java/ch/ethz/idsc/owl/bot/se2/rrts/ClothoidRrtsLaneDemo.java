// code by jph, gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.LidarEmulator;
import ch.ethz.idsc.owl.bot.se2.Se2PointsVsRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.data.TimeKeeper;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.MouseShapeRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.lane.LaneConsumer;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectoryRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SimpleLaneConsumer;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.sim.CameraEmulator;
import ch.ethz.idsc.owl.sim.LidarRaytracer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class ClothoidRrtsLaneDemo implements DemoInterface {
  private static final LidarRaytracer LIDAR_RAYTRACER = //
      new LidarRaytracer(Subdivide.of(Degree.of(-90), Degree.of(90), 32), Subdivide.of(0, 5, 30));
  // ---
  private final LaneConsumptionDemo laneConsumptionDemo;

  public ClothoidRrtsLaneDemo() {
    super();
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(region);
    TransitionRegionQuery transitionRegionQuery = TransitionRegionQueryUnion.wrap( //
        new SampledTransitionRegionQuery(region, RealScalar.of(0.05)), //
        new ClothoidCurvatureQuery(Clips.absolute(5.)));
    StateTime stateTime = new StateTime(Tensors.vector(6, 5, Math.PI / 4), RealScalar.ZERO);
    ClothoidLaneRrtsEntity entity = new ClothoidLaneRrtsEntity(stateTime, transitionRegionQuery, Tensors.vector(0, 0), r2ImageRegionWrap.range(), true);
    LaneConsumer laneConsumer = new SimpleLaneConsumer(entity, null, Collections.singleton(entity));
    laneConsumptionDemo = new LaneConsumptionDemo(laneConsumer);
    laneConsumptionDemo.setControlPointsSe2(Tensors.of(stateTime.state()));
    laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(RegionRenders.create(region));
    laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterface(entity);
    /** TODO GJOEL rework; currently taken over from {@link OwlyAnimationFrame}
     * shorter variant, that does not close properly
     * OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
     * owlyAnimationFrame.add(entity); */
    {
      Timer timer = new Timer();
      { // periodic task for rendering
        TimerTask timerTask = new TimerTask() {
          @Override
          public void run() {
            laneConsumptionDemo.timerFrame.geometricComponent.jComponent.repaint();
          }
        };
        timer.schedule(timerTask, 100, 50);
      }
      laneConsumptionDemo.timerFrame.jFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent windowEvent) {
          timer.cancel();
        }
      });
      { // periodic task for integration
        TimerTask timerTask = new TimerTask() {
          TimeKeeper timeKeeper = new TimeKeeper();

          @Override
          public void run() {
            Scalar now = timeKeeper.now();
            entity.integrate(now);
          }
        };
        timer.schedule(timerTask, 100, 20);
      }
    }
    {
      JToggleButton jToggleButton = new JToggleButton("cone");
      jToggleButton.addActionListener(event -> entity.setConical(jToggleButton.isSelected()));
      laneConsumptionDemo.timerFrame.jToolBar.addSeparator();
      laneConsumptionDemo.timerFrame.jToolBar.add(jToggleButton);
    }
    {
      RenderInterface renderInterface = new CameraEmulator( //
          48, RealScalar.of(10), entity::getStateTimeNow, trajectoryRegionQuery);
      laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new LidarEmulator( //
          LIDAR_RAYTRACER, entity::getStateTimeNow, trajectoryRegionQuery);
      laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    }
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), region)), //
          ClothoidRrtsEntity.SHAPE, () -> entity.getStateTimeNow().time());
      laneConsumptionDemo.timerFrame.geometricComponent.addRenderInterfaceBackground(renderInterface);
    }
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    BaseFrame baseFrame = laneConsumptionDemo.start();
    baseFrame.geometricComponent.setOffset(50, 700);
    baseFrame.jFrame.setBounds(100, 100, 1200, 900);
    baseFrame.jFrame.setTitle(getClass().getSimpleName());
    return baseFrame;
  }

  public static void main(String[] args) {
    new ClothoidRrtsLaneDemo().start().jFrame.setVisible(true);
  }
}
