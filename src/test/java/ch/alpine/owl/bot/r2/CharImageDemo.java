// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.alpine.tensor.ext.HomeDirectory;

enum CharImageDemo {
  ;
  // demo
  static void main() throws IOException {
    CharImage charImage = CharImage.fillWhite(new Dimension(236, 180));
    charImage.setFont(new Font(Font.DIALOG, Font.BOLD, 400));
    charImage.draw('a', new Point(-20, 200));
    BufferedImage bufferedImage = charImage.bufferedImage();
    ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures.resolve("circdots.png").toFile());
  }
}
