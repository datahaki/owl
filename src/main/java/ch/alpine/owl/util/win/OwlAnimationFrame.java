// code by jph
package ch.alpine.owl.util.win;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.ani.api.AnimationInterface;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.Timing;

public class OwlAnimationFrame extends TimerFrame {
  private static final Dimension RECORDING = new Dimension(400, 400);
  private static final int MARGIN = 100; // 170;
  // ---
  private final List<AnimationInterface> animationInterfaces = new CopyOnWriteArrayList<>();
  private final JToggleButton jToggleButtonRecord = new JToggleButton("record");
  private final ActionListener actionListener = new ActionListener() {
    TimerTask timerTask;

    @Override
    public void actionPerformed(ActionEvent event) {
      boolean selected = jToggleButtonRecord.isSelected();
      if (selected) {
        // TODO OWL ALG implementation not generic
        TrajectoryEntity abstractEntity = (TrajectoryEntity) animationInterfaces.get(0);
        Path directory = HomeDirectory.Pictures.resolve(abstractEntity.getClass().getSimpleName() + "_" + System.currentTimeMillis());
        try {
          Files.createDirectories(directory);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        timerTask = new TimerTask() {
          int count = 0;
          Point2D point = null;
          private final String IMAGE_FORMAT = "png";

          @Override
          public void run() {
            BufferedImage offscreen = offscreen();
            StateTime stateTime = abstractEntity.getStateTimeNow();
            GeometricLayer geometricLayer = new GeometricLayer(geometricComponent.getModel2Pixel());
            Point2D now = geometricLayer.toPoint2D(stateTime.state());
            // Point now = geometricComponent.toPixel();
            if (Objects.isNull(point) || MARGIN < inftyNorm(point, now))
              point = now;
            Dimension dimension = RECORDING;
            BufferedImage bufferedImage = //
                new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.getGraphics().drawImage(offscreen, //
                (int) (dimension.width / 2 - point.getX()), //
                (int) (dimension.height / 2 - point.getY()), null);
            try {
              ImageIO.write(bufferedImage, IMAGE_FORMAT, directory.resolve( //
                  String.format("owl_%05d.%s", count++, IMAGE_FORMAT)).toFile());
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }
        };
        timer.schedule(timerTask, 100, 100);
      } else
        timerTask.cancel();
    }
  };

  public OwlAnimationFrame() {
    { // periodic task for integration
      TimerTask timerTask = new TimerTask() {
        final Timing timing = Timing.started();
        // TODO ideally use units
        final ScalarUnaryOperator IN_SEC = QuantityMagnitude.SI().in("s");

        @Override
        public void run() {
          Scalar now = IN_SEC.apply(timing.seconds());
          animationInterfaces.forEach(animationInterface -> animationInterface.integrate(now));
        }
      };
      timer.schedule(timerTask, 100, 20);
    }
    {
      jToggleButtonRecord.addActionListener(actionListener);
      jToolBar.add(jToggleButtonRecord);
    }
  }

  /** @param renderInterface */
  public void addBackground(RenderInterface renderInterface) {
    geometricComponent.addRenderInterfaceBackground(renderInterface);
  }

  public void add(AnimationInterface animationInterface) {
    animationInterfaces.add(animationInterface);
    if (animationInterface instanceof RenderInterface renderInterface)
      geometricComponent.addRenderInterface(renderInterface);
  }

  private static double inftyNorm(Point2D p1, Point2D p2) {
    return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY()));
  }
}
