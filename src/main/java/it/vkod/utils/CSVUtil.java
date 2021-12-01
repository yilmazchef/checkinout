package it.vkod.utils;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class CSVUtil {

  private CSVUtil() {}

  public static void toCsv(final String[] header, final String[][] records) {

    try (Writer writer =
            Files.newBufferedWriter(
                Paths.get(
                    "./exports/" + LocalDate.now().toString().concat(" Aanwezigheidslijst.csv")));
        CSVWriter csvWriter =
            new CSVWriter(
                writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END); ) {

      csvWriter.writeNext(header);

      if (records != null) {
        for (String[] row : records) {
          csvWriter.writeNext(row);
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
