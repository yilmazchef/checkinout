package it.vkod.views;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.util.SharedUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.IntStream;

@Route("import/csv")
public class ImportView extends VerticalLayout {

    private final Grid<String[]> grid = new Grid<>();

    public ImportView() {

        final var buffer = new MemoryBuffer();
        final var upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".csv");
        upload.addSucceededListener(onUploadSucceed -> displayCsv(buffer.getInputStream()));
        add(upload, grid);
    }

    private void displayCsv(InputStream resourceAsStream) {

        final var parser = new CSVParserBuilder().withSeparator(',').build();
        final var reader =
                new CSVReaderBuilder(new InputStreamReader(resourceAsStream)).withCSVParser(parser).build();
        try {
            final var entries = reader.readAll();
            final var headers = entries.get(0);
            grid.removeAllColumns();

            IntStream
                    .range(0, headers.length)
                    .forEachOrdered(colIndex -> grid.addColumn(row -> row[colIndex])
                            .setHeader(SharedUtil.camelCaseToHumanFriendly(headers[colIndex]))
                    );

            grid.setItems(entries.subList(1, entries.size()));

        } catch (IOException | CsvException csvEx) {
            Notification.show(csvEx.getMessage(), 5000, Notification.Position.BOTTOM_CENTER).open();
        }
    }
}