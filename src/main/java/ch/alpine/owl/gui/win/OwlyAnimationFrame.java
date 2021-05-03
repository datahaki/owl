// code by jph
package ch.alpine.owl.gui.win;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

import ch.alpine.owl.ani.api.AnimationInterface;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.data.TimeKeeper;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.HomeDirectory;

public class OwlyAnimationFrame extends TimerFrame {
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
        // TODO JPH implementation not generic
        TrajectoryEntity abstractEntity = (TrajectoryEntity) animationInterfaces.get(0);
        File directory = HomeDirectory.Pictures(abstractEntity.getClass().getSimpleName() + "_" + System.currentTimeMillis());
        directory.mkdir();
        timerTask = new TimerTask() {
          int count = 0;
          Point2D point = null;

          @Override
          public void run() {
            BufferedImage offscreen = offscreen();
            StateTime stateTime = abstractEntity.getStateTimeNow();
            GeometricLayer geometricLayer = GeometricLayer.of(geometricComponent.getModel2Pixel());
            Point2D now = geometricLayer.toPoint2D(stateTime.state());
            // Point now = geometricComponent.toPixel();
            if (Objects.isNull(point) || MARGIN < PointUtil.inftyNorm(point, now))
              point = now;
            Dimension dimension = RECORDING;
            BufferedImage bufferedImage = //
                new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.getGraphics().drawImage(offscreen, //
                (int) (dimension.width / 2 - point.getX()), //
                (int) (dimension.height / 2 - point.getY()), null);
            try {
              ImageIO.write(bufferedImage, IMAGE_FORMAT, new File(directory, //
                  String.format("owly_%05d.%s", count++, IMAGE_FORMAT)));
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

  public OwlyAnimationFrame() {
    { // periodic task for integration
      TimerTask timerTask = new TimerTask() {
        TimeKeeper timeKeeper = new TimeKeeper();

        @Override
        public void run() {
          Scalar now = timeKeeper.now();
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
    if (animationInterface instanceof RenderInterface) {
      RenderInterface renderInterface = (RenderInterface) animationInterface;
      geometricComponent.addRenderInterface(renderInterface);
    }
  }
}
