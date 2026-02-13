// code by jph
package ch.alpine.owl.img;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Jpeg;
import ch.alpine.tensor.ext.MergeIllegal;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.img.ImageRotate;
import ch.alpine.tensor.img.Thumbnail;
import ch.alpine.tensor.io.FileHash;
import ch.alpine.tensor.sca.pow.Sqrt;

class ImageGalleryDemo {
  private static final boolean ASIS = true;
  private static final float JPG_QUALITY = 0.98f;
  private static final int THUMB_SIZE = 100;
  private static final int TARGET_PIXELS = 768 * 1024;

  private static void of(File src, File dst) {
    if (!src.isDirectory())
      throw new IllegalArgumentException(src.toString());
    dst.mkdir();
    if (!dst.isDirectory())
      throw new IllegalArgumentException(dst.toString());
    new ImageGalleryDemo(src, dst);
  }

  private final File dst_thumb;
  private final File dst_image;
  private final Map<File, String> map;

  public ImageGalleryDemo(File src, File dst) {
    dst_thumb = new File(dst, "thumb");
    dst_image = new File(dst, "image");
    dst_thumb.mkdir();
    dst_image.mkdir();
    map = Collections.unmodifiableMap(Arrays.stream(src.listFiles()) //
        .filter(File::isFile) //
        .sorted() //
        .collect(Collectors.toMap(f -> f, ImageGalleryDemo::computeHash, MergeIllegal.operator(), LinkedHashMap::new)));
    // ---
    Arrays.stream(src.listFiles()) //
        .filter(File::isFile) //
        .filter(this::isMissing) //
        .map(File::toPath) //
        .forEach(this::handle);
    // ---
    createIndex();
  }

  private static String computeHash(File file) {
    try {
      return FileHash.string(file.toPath(), MessageDigest.getInstance("MD5")).substring(0, 8);
    } catch (Exception exception) {
      throw new RuntimeException();
    }
  }

  private boolean isMissing(File file) {
    System.out.println(file);
    try {
      String key = map.get(file) + ".jpg";
      boolean exist = true;
      exist &= new File(dst_thumb, key).exists();
      exist &= new File(dst_image, key).exists();
      return !exist;
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return false;
  }

  private void handle(Path file) {
    System.out.println("\\---");
    System.out.println("recompute " + file);
    String key = map.get(file) + ".jpg";
    System.out.println(key);
    try {
      final boolean rotate = MetadataDemo.rotation(file);
      System.out.println("rotation = " + rotate);
      final BufferedImage bufferedImage = ImageIO.read(file.toFile());
      {
        File ifile = new File(dst_thumb, key);
        System.out.println(ifile);
        if (ifile.exists()) {
          System.out.println("skip thumb");
        } else {
          BufferedImage result = Thumbnail.of(bufferedImage, THUMB_SIZE, rotate);
          if (rotate)
            result = ImageRotate.cw(result);
          Jpeg.put(result, ifile.toPath(), JPG_QUALITY);
        }
      }
      {
        File ifile = new File(dst_image, key);
        if (ifile.exists()) {
          System.out.println("skip image");
        } else {
          Scalar pixels = RationalScalar.of( //
              TARGET_PIXELS, //
              Math.multiplyExact(bufferedImage.getHeight(), bufferedImage.getWidth()));
          Scalar factor = Sqrt.FUNCTION.apply(pixels);
          double doubleValue = factor.number().doubleValue();
          BufferedImage result = ASIS ? bufferedImage
              : ImageResize.DEGREE_3.of(bufferedImage, //
                  (int) Math.round(bufferedImage.getWidth() * doubleValue), //
                  (int) Math.round(bufferedImage.getHeight() * doubleValue));
          if (rotate)
            result = ImageRotate.cw(result);
          Jpeg.put(result, ifile.toPath(), JPG_QUALITY);
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private void createIndex() {
    File dst = dst_thumb.getParentFile();
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(dst, "index.html")))) {
      for (String string : ResourceData.lines("/html/gallery/head.html"))
        bufferedWriter.write(string + "\n");
      // ---
      for (String key : map.values()) {
        String name = key + ".jpg";
        bufferedWriter.write("<a href=\"image/" + name + "\"><img src=\"thumb/" + name + "\"></a>\n");
      }
      // ---
      for (String string : ResourceData.lines("/html/gallery/tail.html"))
        bufferedWriter.write(string + "\n");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  static void main() {
    // File src = HomeDirectory.Pictures("boat/trip02");
    // File dst = HomeDirectory.file("public_html/photos/2025_scandinavia");
    Path src = HomeDirectory.Pictures.resolve("2025_barberini", "dst");
    Path dst = HomeDirectory.public_html.resolve("photos", "2025_barberini");
    of(src.toFile(), dst.toFile());
  }
}
