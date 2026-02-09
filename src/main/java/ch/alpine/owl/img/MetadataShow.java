// code by jph
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

public class MetadataShow {
  public MetadataShow(File file) {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file);
      for (Directory directory : metadata.getDirectories()) {
        stringBuilder.append(directory + "\n");
        for (Tag tag : directory.getTags()) {
          stringBuilder.append("\u3000" + tag.getTagName() + "=" + tag.getDescription() + "\n");
        }
      }
    } catch (ImageProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    new StringAreaShow(stringBuilder.toString());
  }

  static void main() {
    Path file = HomeDirectory.Pictures.resolve("2023_ma", "P1240488.JPG");
    file = HomeDirectory.Pictures.resolve("2023_ma", "P1240420.JPG");
    new MetadataShow(file.toFile());
  }
}
