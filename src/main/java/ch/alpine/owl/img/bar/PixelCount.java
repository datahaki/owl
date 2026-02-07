package ch.alpine.owl.img.bar;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.imageio.ImageIO;

import ch.alpine.owl.img.bar.ImagePerspective.Format;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Import;

enum PixelCount {
  ;
  static void main() throws FileNotFoundException, IOException {
    Properties properties = Import.properties(Barberini.PROPFILE);
    for (String key : properties.stringPropertyNames()) {
      Tensor points = Tensors.fromString(properties.getProperty(key));
      Format format = new Format(points, BarberiniRun.PIXELS);
      int total = format.w() * format.h();
      System.out.println(total);
    }
    System.out.println("---");
    for (Path file : Files.list(Barberini.DST).toList()) {
      BufferedImage image = ImageIO.read(file.toFile());
      System.out.println(image.getHeight() * image.getWidth());
    }
  }
}
