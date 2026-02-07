// code by jph
package ch.alpine.owl.bot.kl;

import java.io.IOException;
import java.nio.file.Path;

import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Import;

enum KlotskiSolutions {
  ;
  public static void run() throws IOException {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      Path file = KlotskiDemo.solutionFile(klotskiProblem);
      try {
        Import.object(file);
      } catch (Exception exception) {
        KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
        KlotskiSolution klotskiSolution = klotskiDemo.compute();
        Export.object(file, klotskiSolution);
        klotskiDemo.close();
      }
    }
  }

  static void main() throws IOException {
    run();
  }
}
