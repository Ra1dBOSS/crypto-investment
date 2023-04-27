package com.example.cryptoinvestment.csv;

import com.example.cryptoinvestment.csv.model.PriceValue;
import com.example.cryptoinvestment.services.CryptoService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

    private static final Logger LOGGER = LogManager.getLogger(CryptoService.class);

    public List<PriceValue> parse(String csvFileName) {
        var parsed = new ArrayList<PriceValue>();
        try (BufferedReader br = new BufferedReader(new FileReader(new ClassPathResource(csvFileName).getFile()))) {
            final int NUMBER_OF_HEADER_LINES = 1;
            for (int i = 0; i < NUMBER_OF_HEADER_LINES; i++) {
                br.readLine();
            }

            String line;
            while ((line = br.readLine()) != null) {
                final String CVS_SPLIT_BY = ",";
                var data = line.split(CVS_SPLIT_BY);

                final int NAME_INDEX = 1;
                final int TIMESTAMP_INDEX = 0;
                final int PRICE_INDEX = 2;
                parsed.add(new PriceValue(
                        data[NAME_INDEX],
                        Double.parseDouble(data[PRICE_INDEX]),
                        Long.parseLong(data[TIMESTAMP_INDEX])));
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
        return parsed;
    }
}
