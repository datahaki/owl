// code by jph
package ch.alpine.sophus.demo.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.StringScalarQ;

public class UzhSe3TxtFormatTest {
  @Test
  public void testSimple() throws FileNotFoundException, IOException {
    File file = new File("/media/datahaki/media/resource/uzh/groundtruth", "outdoor_forward_5_davis.txt");
    if (file.isFile()) {
      Tensor tensor = UzhSe3TxtFormat.of(file);
      assertEquals(Dimensions.of(tensor), Arrays.asList(22294, 4, 4));
      assertFalse(StringScalarQ.any(tensor));
    }
  }
}
