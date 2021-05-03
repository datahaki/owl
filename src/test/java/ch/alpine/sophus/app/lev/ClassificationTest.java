// code by jph
package ch.alpine.sophus.app.lev;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClassificationTest extends TestCase {
  public void testSimple() {
    Classification classification = Classifier.accMax(Tensors.vector(3, 3, 2, 4));
    Tensor weights = Tensors.vector(0.7, 0.2, 0.3, 0.8);
    ClassificationResult labelResult = classification.result(weights);
    assertEquals(labelResult.getLabel(), 3);
    // Chop._12.requireClose(labelResult.getConfidence(), RealScalar.of(0.45));
  }

  public void testArgMax() {
    Classification classification = Classifier.argMax(Tensors.vector(3, 3, 2, 4));
    Tensor weights = Tensors.vector(0.7, 0.2, 0.3, 0.8);
    ClassificationResult classificationResult = classification.result(weights);
    assertEquals(classificationResult.getLabel(), 4);
    Chop._12.requireClose(classificationResult.getConfidence(), RealScalar.of(0.125));
  }

  public void testSimple2() {
    Classification classification = Classifier.accMax(Tensors.vector(3, 2, 4));
    ClassificationResult classificationResult = classification.result(Tensors.vector(0.2, 0.3, 0.8));
    assertEquals(classificationResult.getLabel(), 4);
    Chop._12.requireClose(classificationResult.getConfidence(), RealScalar.of(0.375));
  }
}
