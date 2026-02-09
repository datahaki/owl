// code by jph
package ch.alpine.owl.img.bar;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import ch.alpine.owl.img.PhotoImport;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Jpeg;
import ch.alpine.tensor.io.Import;

public enum BarberiniRun {
  ;
  public static final int PIXELS = 500_000;

  static void main() throws FileNotFoundException, IOException {
    Properties properties = Import.properties(Barberini.PROPFILE);
    Files.createDirectories(Barberini.DST);
    for (String key : properties.stringPropertyNames()) {
      Path src = Barberini.SRC.resolve(key);
      Path dst = Barberini.DST.resolve("s" + key);
      if (Files.isRegularFile(src) && !Files.isRegularFile(dst)) {
        try {
          System.out.println(key);
          Tensor points = Tensors.fromString(properties.getProperty(key));
          BufferedImage bufferedImage = PhotoImport.of(src);
          System.out.println("IMPORT " + bufferedImage.getHeight() + " " + bufferedImage.getWidth());
          BufferedImage result = ImagePerspective.rectify(bufferedImage, points, PIXELS);
          Jpeg.put(result, dst, 1);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
