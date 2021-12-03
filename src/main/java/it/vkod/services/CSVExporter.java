package it.vkod.services;

import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import it.vkod.data.dto.ChecksGridData;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.opencsv.ICSVWriter.DEFAULT_SEPARATOR;
import static com.opencsv.ICSVWriter.NO_QUOTE_CHARACTER;

@Component
public class CSVExporter {

    public void export(HttpServletResponse response, List<ChecksGridData> data) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        //create a csv writer
        final var writer = new StatefulBeanToCsvBuilder<ChecksGridData>(response.getWriter())
                .withQuotechar(NO_QUOTE_CHARACTER)
                .withSeparator(DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .build();

        //write all data to csv file
        writer.write(data);

    }
}
