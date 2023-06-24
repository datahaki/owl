// code by jph
package ch.alpine.ubongo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import ch.alpine.tensor.ext.HomeDirectory;

public enum UbongoImaging {
  ;
  public static void main(String[] args) throws IOException {
    List<UbongoPublish> list2 = Arrays.stream(UbongoPublish.values()) //
        .filter(u -> u.ubongoBoards.use() >= 9) //
        .toList();
    for (UbongoPublish ubongoPublish : list2) {
      BufferedImage bufferedImage = new BufferedImage(700, 900, BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      // 68 was too large: 17.95 instead of 16
      // 61 was tested to work well
      StaticHelper.draw(graphics, ubongoPublish, 61);
      graphics.dispose();
      File folder = HomeDirectory.Pictures("ubongo8");
      folder.mkdir();
      File file = new File(folder, ubongoPublish.name() + ".png");
      ImageIO.write(bufferedImage, "png", file);
    }
  }
}
