package ch.alpine.tensor.demo;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.Pretty;

public enum TableCalendar {
  ;
  public static void main(String[] args) throws IOException {
    LocalDate beg = LocalDate.of(2022, Month.AUGUST, 28);
    LocalDate end = LocalDate.of(2023, Month.APRIL, 2);
    Tensor table = Tensors.empty();
    Tensor row = Tensors.empty();
    while (!beg.equals(end)) {
      row.append(RealScalar.of(beg.getDayOfMonth()));
      DayOfWeek dayOfWeek = beg.getDayOfWeek();
      if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
        table.append(row);
        row = Tensors.empty();
      }
      beg = beg.plusDays(1);
      System.out.println(beg);
    }
    if (row.length() != 0)
      table.append(row);
    System.out.println(Pretty.of(table));
    Export.of(HomeDirectory.file("cal.csv"), table);
  }
}
