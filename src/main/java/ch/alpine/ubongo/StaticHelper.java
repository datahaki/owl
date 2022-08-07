// code by jph
package ch.alpine.ubongo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import ch.alpine.bridge.awt.RenderQuality;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.img.ImageRotate;

/* package */ enum StaticHelper {
  ;
  private static final int MARGIN_X = 320;
  private static final int MARGIN_Y = 13;
  // 61.1465
  static final int SCALE = 46;
  private static final int ZCALE = 10;
  private static final int MAX_X = 10;
  private static final int MAX_Y = 8;
  private static final Color FILL = Color.LIGHT_GRAY;

  public static int maxWidth() {
    return MARGIN_X + MAX_X * SCALE + 1;
  }

  public static int maxHeight() {
    return MAX_Y * SCALE + MARGIN_Y * 2;
  }

  public static void draw(Graphics2D graphics, UbongoPublish ubongoPublish, int SCALE) {
    int piy = MARGIN_Y;
    {
      List<List<UbongoEntry>> solutions = UbongoLoader.INSTANCE.load(ubongoPublish.ubongoBoards);
      int count = 0;
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
      for (int index : ubongoPublish.list) {
        ++count;
        graphics.setColor(Color.DARK_GRAY);
        int pix = 50;
        {
          Graphics2D g = (Graphics2D) graphics.create();
          RenderQuality.setQuality(g);
          g.drawString("" + count, 2, piy + 20);
          g.dispose();
        }
        List<UbongoEntry> solution = solutions.get(index);
        for (UbongoEntry ubongoEntry : solution) {
          UbongoEntry ubongoPiece = new UbongoEntry();
          ubongoPiece.stamp = ImageRotate.cw(ubongoEntry.ubongo.mask());
          ubongoPiece.ubongo = ubongoEntry.ubongo;
          List<Integer> size = Dimensions.of(ubongoPiece.stamp);
          int piw = size.get(1) * ZCALE;
          int scale = ZCALE;
          Tensor mask = ubongoPiece.stamp;
          graphics.setColor(FILL);
          for (int row = 0; row < size.get(0); ++row)
            for (int col = 0; col < size.get(1); ++col) {
              Scalar scalar = mask.Get(row, col);
              if (Scalars.nonZero(scalar))
                graphics.fillRect(pix + col * scale, piy + row * scale, scale, scale);
            }
          pix += piw + 2 * ZCALE;
        }
        piy += 4 * ZCALE + 2 * ZCALE;
      }
    }
    {
      UbongoBoard ubongoBoard = ubongoPublish.ubongoBoards.board();
      Tensor mask = ubongoBoard.mask();
      int scale = SCALE;
      int marginX = 0;
      int marginY = piy + scale;
      graphics.setColor(FILL);
      List<Integer> size = Dimensions.of(mask);
      for (int row = 0; row < size.get(0); ++row)
        for (int col = 0; col < size.get(1); ++col) {
          Scalar scalar = mask.Get(row, col);
          if (Scalars.nonZero(scalar))
            graphics.fillRect(marginX + col * scale, marginY + row * scale, scale - 1, scale - 1);
        }
    }
  }
}
