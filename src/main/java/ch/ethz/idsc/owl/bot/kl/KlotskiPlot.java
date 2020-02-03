// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ class KlotskiPlot {
  private static final Color STONE_GOAL = new Color(128 - 32, 128 - 32, 255);
  private static final Color STONE_MISC = new Color(128 + 32, 128 + 32, 255);
  static final double MARGIN = 0.08;
  static final Tensor BLOCKS = Tensors.of( //
      Tensors.vector(2, 2), //
      Tensors.vector(2, 1), //
      Tensors.vector(1, 2), //
      Tensors.vector(1, 1), //
      Tensors.vector(3, 1));
  // ---
  private final int RES;
  private final Tensor border;
  private final int sx;
  private final int sy;

  public KlotskiPlot(KlotskiProblem klotskiProblem, int res) {
    Tensor size = klotskiProblem.size();
    sx = size.Get(0).number().intValue();
    sy = size.Get(1).number().intValue();
    this.border = klotskiProblem.getFrame();
    this.RES = res;
  }

  BufferedImage plot(Tensor board) {
    BufferedImage bufferedImage = new BufferedImage(sy * RES, sx * RES, BufferedImage.TYPE_INT_ARGB);
    GeometricLayer geometricLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
        { 0, RES, 0 }, //
        { RES, 0, 0 }, //
        { 0, 0, 1 } }));
    Graphics2D graphics = bufferedImage.createGraphics();
    new Plot(board).render(geometricLayer, graphics);
    return bufferedImage;
  }

  class Plot implements RenderInterface {
    private final Tensor board;

    Plot(Tensor board) {
      this.board = Objects.requireNonNull(board);
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, sy * RES, sx * RES);
      graphics.setColor(new Color(128, 128, 255));
      graphics.fill(geometricLayer.toPath2D(border));
      for (Tensor stone : board) {
        int index = stone.Get(0).number().intValue();
        geometricLayer.pushMatrix(Se2Matrix.translation(stone.extract(1, 3)));
        graphics.setColor(index == 0 ? STONE_GOAL : STONE_MISC);
        {
          Tensor polygon = Tensors.empty();
          int limit = BLOCKS.length();
          if (index < limit) {
            Tensor format = BLOCKS.get(index);
            polygon = Tensors.of( //
                format.pmul(Tensors.vector(0, 0)).add(Tensors.vector(+MARGIN, +MARGIN)), //
                format.pmul(Tensors.vector(0, 1)).add(Tensors.vector(+MARGIN, -MARGIN)), //
                format.pmul(Tensors.vector(1, 1)).add(Tensors.vector(-MARGIN, -MARGIN)), //
                format.pmul(Tensors.vector(1, 0)).add(Tensors.vector(-MARGIN, +MARGIN)));
          } else {
            switch (index) {
            case 5:
              polygon = Tensors.of( //
                  Tensors.vector(0 + MARGIN, 0 + MARGIN), //
                  Tensors.vector(2 - MARGIN, 0 + MARGIN), //
                  Tensors.vector(2 - MARGIN, 1 - MARGIN), //
                  Tensors.vector(1 - MARGIN, 1 - MARGIN), //
                  Tensors.vector(1 - MARGIN, 2 - MARGIN), //
                  Tensors.vector(0 + MARGIN, 2 - MARGIN));
              break;
            case 6:
              polygon = Tensors.of( //
                  Tensors.vector(2 - MARGIN, 2 - MARGIN), //
                  Tensors.vector(0 + MARGIN, 2 - MARGIN), //
                  Tensors.vector(0 + MARGIN, 1 + MARGIN), //
                  Tensors.vector(1 + MARGIN, 1 + MARGIN), //
                  Tensors.vector(1 + MARGIN, 0 + MARGIN), //
                  Tensors.vector(2 - MARGIN, 0 + MARGIN));
              break;
            default:
              throw new RuntimeException("index=" + index);
            }
          }
          graphics.fill(geometricLayer.toPath2D(polygon));
        }
        geometricLayer.popMatrix();
      }
    }
  }

  public static void export(KlotskiSolution klotskiSolution) throws IOException {
    KlotskiProblem klotskiProblem = klotskiSolution.klotskiProblem;
    List<StateTime> list = klotskiSolution.list;
    System.out.println(list.size());
    int index = 0;
    File folder = HomeDirectory.Pictures(klotskiProblem.name());
    folder.mkdir();
    int RES = 128;
    KlotskiPlot klotskiPlot = new KlotskiPlot(klotskiProblem, RES);
    for (StateTime stateTime : list) {
      BufferedImage bufferedImage = klotskiPlot.plot(stateTime.state());
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.DARK_GRAY);
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, RES / 2));
      graphics.drawString("move " + index, RES / 8, RES / 2);
      ImageIO.write(bufferedImage, "png", new File(folder, String.format("%03d.png", index)));
      ++index;
    }
  }
}
