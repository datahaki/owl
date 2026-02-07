package ch.alpine.owl.img;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import ch.alpine.tensor.ext.HomeDirectory;

public class MetadataBulk {
  private final Map<String, Set<String>> map = new LinkedHashMap<>();

  public void handle(Path file) {
    try {
      Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());
      for (Directory directory : metadata.getDirectories()) {
        for (Tag tag : directory.getTags()) {
          String key = directory.toString() + "." + tag.getTagName();
          // System.out.println(key);
          String value = tag.getDescription();
          if (Objects.nonNull(value)) {
            // System.out.println(value);
            if (!map.containsKey(key))
              map.put(key, new TreeSet<>());
            map.get(key).add(value);
          }
        }
      }
    } catch (ImageProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String show() {
    StringBuilder stringBuilder = new StringBuilder();
    for (Entry<String, Set<String>> entry : map.entrySet()) {
      Set<String> set = entry.getValue();
      if (set.size() != 1) {
        stringBuilder.append(entry.getKey() + "\n");
        for (String string : entry.getValue())
          stringBuilder.append("\u3000" + string + "\n");
      }
    }
    return stringBuilder.toString();
  }

  static void main() throws IOException {
    MetadataBulk metadataBulk = new MetadataBulk();
    Path dir = HomeDirectory.Pictures.resolve("2023_ma");
    for (Path file : Files.list(dir).toList()) {
      System.out.println(file);
      metadataBulk.handle(file);
    }
    String string = metadataBulk.show();
    new StringAreaShow(string);
  }
}
