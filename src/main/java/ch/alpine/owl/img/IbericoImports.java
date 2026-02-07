package ch.alpine.owl.img;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

class IbericoImports {
  private static Tensor date() {
    Tensor tensor = Tensors.empty();
    try {
      List<String> list = ReadLine.of(Files.newInputStream(HomeDirectory.Documents.resolve("2024_stats.csv"))).collect(Collectors.toList());
      for (String s : list) {
        // System.out.println(s);
        String[] strings = s.split(",");
        int tokens = strings.length;
        if (tokens == 9) {
          Scalar date = Scalars.fromString(strings[0] + "T00:00");
          if (!date.toString().substring(0, 10).equals(strings[0]))
            System.err.println(date);
          Scalar length = Scalars.fromString(strings[2] + "[km]");
          Scalar increm = Scalars.fromString(strings[3] + "[m]").add(Scalars.fromString("0" + strings[4] + "[m]"));
          tensor.append( //
              Tensors.of( //
                  date, //
                  length, //
                  increm, //
                  Tensors.of( //
                      Scalars.fromString(strings[5].replace('\"', ' ')), //
                      Scalars.fromString(strings[6].replace('\"', ' '))), //
                  Tensors.of( //
                      Scalars.fromString(strings[7].replace('\"', ' ')), //
                      Scalars.fromString(strings[8].replace('\"', ' ')))) //
          );
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return tensor;
  }

  final List<IbericoWinter> winter = new ArrayList<>();

  public IbericoImports(boolean days) {
    Tensor tensor = date();
    Clip w1 = Clips.interval( //
        DateTime.of(2023, 1, 1, 0, 0), //
        DateTime.of(2024, 3, 15, 0, 0));
    Clip w2 = Clips.interval( //
        DateTime.of(2024, 3, 16, 0, 0), //
        DateTime.of(2024, 10, 25, 0, 0));
    Clip w3 = Clips.interval( //
        DateTime.of(2024, 10, 26, 0, 0), //
        DateTime.of(2025, 4, 1, 0, 0));
    winter.add(new IbericoWinter(days, Tensor.of(tensor.stream().filter(r -> w1.isInside(r.Get(0))))));
    winter.add(new IbericoWinter(days, Tensor.of(tensor.stream().filter(r -> w2.isInside(r.Get(0))))));
    winter.add(new IbericoWinter(days, Tensor.of(tensor.stream().filter(r -> w3.isInside(r.Get(0))))));
    int sum = winter.stream().mapToInt(i -> i.tensor.length()).sum();
    Integers.requireEquals(sum, tensor.length());
    for (IbericoWinter ii : winter) {
      System.out.println("here " + ii);
    }
  }
}
