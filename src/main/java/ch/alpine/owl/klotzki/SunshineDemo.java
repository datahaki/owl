// code by jph
package ch.alpine.owl.klotzki;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.red.Times;

/* package */ enum SunshineDemo {
  ;
  static void main() throws IOException {
    KlotskiProblem klotskiProblem = Sunshine.ORIGINAL.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    klotskiDemo.runStandalone();
    IO.println("NON BLOCKING");
    Tensor model2Pixel = klotskiDemo.timerFrame.geometricComponent.getModel2Pixel();
    klotskiDemo.timerFrame.geometricComponent.setModel2Pixel(Times.of(Tensors.vector(0.4, 0.4, 1), model2Pixel));
    // klotskiDemo.timerFrame.geometricComponent.setOffset(100, 600);
    KlotskiSolution klotskiSolution = klotskiDemo.compute();
    Export.object(KlotskiDemo.solutionFile(klotskiProblem), klotskiSolution);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiSolution);
  }
}
