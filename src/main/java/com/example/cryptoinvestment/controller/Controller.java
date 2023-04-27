package com.example.cryptoinvestment.controller;

import com.example.cryptoinvestment.controller.model.IndicatorsDto;
import com.example.cryptoinvestment.persistence.entities.CryptoEntity;
import com.example.cryptoinvestment.services.CryptoService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class Controller {

    private final CryptoService cryptoService;

    @GetMapping("/{cryptoName}/indicators")
    IndicatorsDto getPrices(@PathVariable String cryptoName) {
        return cryptoService.getCryptoIndicators(cryptoName);
    }

    @GetMapping("/crypto-with-highest-normalized-range/{date}")
    public String getCryptoWithHighestNormalizedRange(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return cryptoService.getCryptoNameWithHighestNormalizedRange(date);
    }

    @GetMapping("/cryptos-ordered-by-normalized-range")
    public List<String> getCryptosOrderedByNormalizedRange() {
        return cryptoService.getCryptosOrderedByNormalizedRange();
    }
}
