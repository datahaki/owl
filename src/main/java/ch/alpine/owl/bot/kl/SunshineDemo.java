// code by jph
package ch.alpine.owl.bot.kl;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.Export;

/* package */ enum SunshineDemo {
  ;
  public static void main(String[] args) throws IOException {
    KlotskiProblem klotskiProblem = Sunshine.ORIGINAL.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    Tensor model2Pixel = klotskiDemo.klotskiFrame.timerFrame.geometricComponent.getModel2Pixel();
    klotskiDemo.klotskiFrame.timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(0.4, 0.4, 1).pmul(model2Pixel));
    klotskiDemo.klotskiFrame.timerFrame.geometricComponent.setOffset(100, 600);
    KlotskiSolution klotskiSolution = klotskiDemo.compute();
    Export.object(KlotskiDemo.solutionFile(klotskiProblem), klotskiSolution);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiSolution);
  }
}
