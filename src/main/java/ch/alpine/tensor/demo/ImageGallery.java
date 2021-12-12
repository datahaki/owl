// code by jph
package ch.alpine.tensor.demo;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.img.Thumbnail;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.FileHash;
import ch.alpine.tensor.io.Import;
import ch.alpine.tensor.sca.Sqrt;

public class ImageGallery {
  private final Map<File, String> map = new HashMap<>();
  private final File dst_thumb;
  private final File dst_image;

  public ImageGallery(File src, File dst) {
    dst.mkdir();
    dst_thumb = new File(dst, "thumb");
    dst_image = new File(dst, "image");
    dst_thumb.mkdir();
    dst_image.mkdir();
    Stream.of(src.listFiles()) //
        .filter(this::isMissing) //
        .parallel() //
        .forEach(this::handle);
  }

  private boolean isMissing(File file) {
    if (file.isFile())
      try {
        String key = FileHash.string(file, MessageDigest.getInstance("MD5")).substring(0, 8) + ".jpg";
        map.put(file, key);
        return !new File(dst_thumb, key).exists() || !new File(dst_image, key).exists();
      } catch (Exception e) {
        e.printStackTrace();
      }
    return false;
  }

  private void handle(File file) {
    String key = map.get(file);
    Tensor image = null;
    try {
      {
        File ifile = new File(dst_thumb, key);
        if (!ifile.exists()) {
          System.out.println("read image " + file.getName());
          image = read(file);
          System.out.println("thumbnail " + key);
          Export.of(ifile, Thumbnail.of(image, 100));
        }
      }
      {
        File ifile = new File(dst_image, key);
        if (!ifile.exists()) {
          if (image == null)
            image = read(file);
          List<Integer> list = Dimensions.of(image);
          Scalar pixels = RationalScalar.of( //
              768 * 1024, //
              Math.multiplyExact(list.get(0), list.get(1)));
          Tensor result = ImageResize.of(image, Sqrt.FUNCTION.apply(pixels));
          Export.of(ifile, result);
        }
      }
      // BufferedImage bufferedImage = ImageIO.read(file);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Tensor read(File file) throws IOException {
    return Import.of(file);
  }

  public static void main(String[] args) {
    File src = new File("/run/media/datahaki/data/pictures/2021_12/best_of");
    File dst = new File("/run/media/datahaki/data/public_html/photos/2021_poland");
    new ImageGallery(src, dst);
  }
}
