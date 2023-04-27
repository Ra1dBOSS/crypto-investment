package com.example.cryptoinvestment.services;

import com.example.cryptoinvestment.controller.model.IndicatorsDto;
import com.example.cryptoinvestment.csv.CsvParser;
import com.example.cryptoinvestment.csv.model.PriceValue;
import com.example.cryptoinvestment.persistence.CryptoRepository;
import com.example.cryptoinvestment.persistence.PriceRepository;
import com.example.cryptoinvestment.persistence.entities.CryptoEntity;
import com.example.cryptoinvestment.persistence.entities.PriceEntity;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CryptoService {

    private static final Logger LOGGER = LogManager.getLogger(CryptoService.class);

    private static final String FILE_POSTFIX = "_values.csv";
    private static final String FOLDER_PREFIX = "prices/";

    private final PriceRepository priceRepository;
    private final CryptoRepository cryptoRepository;
    private final CsvParser parser;

    @PostConstruct
    public void init() {
        loadAllPriceValuesIfNeed();
    }

    public IndicatorsDto getCryptoIndicators(String name) {
        if (needUpdate(name)) {
            loadPriceValues(name + FILE_POSTFIX);
        }
        return cryptoRepository.findById(name).map(crypto -> new IndicatorsDto(crypto.getName(), crypto.getMin(), crypto.getMax(), crypto.getOldest(), crypto.getNewest())).orElse(new IndicatorsDto());
    }

    public String getCryptoNameWithHighestNormalizedRange(LocalDate date) {
        loadAllPriceValuesIfNeed();
        long start = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long finish = date.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC).toEpochMilli();
        return priceRepository.getNameWithHighestNormalizedRange(start, finish);
    }

    public List<String> getCryptosOrderedByNormalizedRange() {
        loadAllPriceValuesIfNeed();
        return priceRepository.getNamesOrderedByNormalizedRange();
    }

    @Cacheable(value = "cryptoNames", key = "#name")
    private boolean isSupported(String name) {
        return cryptoRepository.existsById(name) || hasFileWith(name);
    }

    private boolean needUpdate(String name) {
        long fileModified = 0;
        try {
            fileModified = new ClassPathResource(
                    FOLDER_PREFIX + name + (name.endsWith(FILE_POSTFIX) ? "" : FILE_POSTFIX)).lastModified();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
        return isSupported(name)
                && cryptoRepository.getUpdatedAt(name).orElse(Timestamp.valueOf("1990-05-01 00:00:00")).getTime()
                < fileModified;
    }

    private void loadAllPriceValuesIfNeed() {
        try {
            Stream.of(Objects.requireNonNull(new ClassPathResource(FOLDER_PREFIX)
                            .getFile()
                            .listFiles(f -> f.getName().endsWith(FILE_POSTFIX)))
                    )
                    .map(File::getName)
                    .filter(this::needUpdate)
                    .forEach(this::loadPriceValues);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e);
        }
    }

    private void loadPriceValues(String fileName) {
        var name = fileName.substring(0, fileName.indexOf(FILE_POSTFIX));
        cryptoRepository.deleteById(name);
        CryptoEntity cryptoEntity = new CryptoEntity();
        cryptoEntity.setName(name);
        cryptoEntity.setPriceValues(new HashSet<>());
        for (var i : parser.parse(FOLDER_PREFIX + fileName)) {
            var price = map(i);
            cryptoEntity.getPriceValues().add(price);
        }
        calculateIndicators(cryptoEntity);
        cryptoRepository.saveAndFlush(cryptoEntity);
        priceRepository.flush();
    }

    private void calculateIndicators(CryptoEntity crypto) {
        var prices = crypto.getPriceValues();
        long oldestPriceTimestamp = Long.MAX_VALUE;
        long newestPriceTimestamp = 0;
        long startTimeMark = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),
                        ZoneId.systemDefault())
                .minus(1, ChronoUnit.MONTHS)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        for (PriceEntity x : prices) {
            if (x.getTimestamp() > startTimeMark) {
                if (crypto.getMin() == null || crypto.getMin() > x.getPrice()) {
                    crypto.setMin(x.getPrice());
                }
                if (crypto.getMax() == null || crypto.getMax() > x.getPrice()) {
                    crypto.setMax(x.getPrice());
                }
                if (oldestPriceTimestamp > x.getTimestamp()) {
                    oldestPriceTimestamp = x.getTimestamp();
                    crypto.setOldest(x.getPrice());
                }
                if (newestPriceTimestamp < x.getTimestamp()) {
                    newestPriceTimestamp = x.getTimestamp();
                    crypto.setNewest(x.getPrice());
                }
            }
        }
    }

    private boolean hasFileWith(String prefix) {
        return new ClassPathResource(FOLDER_PREFIX + prefix + FILE_POSTFIX).exists();
    }

    private PriceEntity map(PriceValue price) {
        return new PriceEntity(price.getName(), price.getPrice(), price.getTimestamp());
    }
}
