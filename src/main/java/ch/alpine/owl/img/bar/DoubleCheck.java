// code by jph
package ch.alpine.owl.img.bar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

enum DoubleCheck {
  ;
  static void main() throws IOException {
    Set<String> s1 = Files.list(Barberini.SRC).map(Path::getFileName).map(Object::toString).collect(Collectors.toSet());
    Set<String> s2 = Files.list(Barberini.DST).map(Path::getFileName).map(Object::toString).collect(Collectors.toSet());
    for (String f1 : s1) {
      if (s2.contains("s" + f1)) {
        // System.out.println("HERE");
      } else {
        System.out.println(f1);
        System.out.println("NO");
      }
    }
  }
}
