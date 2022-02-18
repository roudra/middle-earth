package org.roy.controller;

import com.opencsv.bean.CsvToBeanBuilder;
import org.roy.model.Trip;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class FileController {

    public List<Trip> processCsv(Path filePath) throws FileNotFoundException {
        return new CsvToBeanBuilder<Trip>(new FileReader(filePath.toFile())).withType(Trip.class).build().parse();
    }


}
