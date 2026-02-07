package ch.alpine.owl.img;

import java.io.File;

public record FindFile(String name) {
  void recur(File dir) {
    for (File file : dir.listFiles()) {
      if (file.getName().equalsIgnoreCase(name))
        System.out.println(file.getAbsolutePath());
      if (file.isDirectory())
        recur(file);
    }
  }

  static void main() {
    FindFile findFile = new FindFile("iberico_match.tsv");
    findFile.recur(new File("/run/media/datahaki/T7"));
    // findFile.recur(HomeDirectory.file());
  }
}
