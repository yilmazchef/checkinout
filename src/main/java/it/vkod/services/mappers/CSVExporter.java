package it.vkod.services.mappers;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import it.vkod.models.entities.Check;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.opencsv.CSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.CSVWriter.DEFAULT_LINE_END;
import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

@Component
public class CSVExporter {

    public void export(HttpServletResponse response, List< Check > data) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        //create a csv writer
        final var writer = new StatefulBeanToCsvBuilder<Check>(response.getWriter())
                .withQuotechar(NO_QUOTE_CHARACTER)
                .withSeparator(DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

        //write all data to csv file
        writer.write(data);

    }

    public void export(HttpServletResponse response, final String[] header, final String[][] records) throws IOException {

        final var csvWriter = new CSVWriter(response.getWriter(), DEFAULT_SEPARATOR, NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);

        csvWriter.writeNext(header);

        if (records != null) {
            for (String[] row : records) {
                csvWriter.writeNext(row);
            }
        }

    }
}
