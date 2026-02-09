// code by jph
package ch.alpine.owl.img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import ch.alpine.tensor.img.ImageRotate;

public enum PhotoImport {
  ;
  public static BufferedImage of(Path file) throws IOException {
    final boolean rotate = MetadataDemo.rotation(file);
    System.out.println("rotation = " + rotate);
    BufferedImage result = ImageIO.read(file.toFile());
    if (rotate)
      result = ImageRotate.cw(result);
    return result;
  }
}
