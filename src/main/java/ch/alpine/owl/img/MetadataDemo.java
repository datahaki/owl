package ch.alpine.owl.img;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import ch.alpine.tensor.ext.HomeDirectory;

public enum MetadataDemo {
  ;
  static void main() {
    show(HomeDirectory.Pictures.resolve("2023_ma", "P1240488.JPG").toFile());
    show(HomeDirectory.Pictures.resolve("2023_ma", "P1240420.JPG").toFile());
  }

  private static void show(File file) {
    System.out.println("======================");
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file);
      for (Directory d : metadata.getDirectories())
        if (d.getName().equals("Exif IFD0")) {
          System.out.println(d);
          for (Tag tag : d.getTags())
            if (tag.getTagName().equals("Orientation")) {
              System.out.println(" \\- " + tag.getDescription());
            }
        }
      System.out.println(metadata.toString());
    } catch (ImageProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static boolean rotation(Path file) {
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
      for (Directory d : metadata.getDirectories())
        if (d.getName().equals("Exif IFD0")) {
          System.out.println(d);
          for (Tag tag : d.getTags())
            if (tag.getTagName().equals("Orientation")) {
              String description = tag.getDescription();
              System.out.println(description);
              // Top, left side (Horizontal / normal)
              return description.equals("Right side, top (Rotate 90 CW)");
            }
        }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return false;
  }
}
