package com.gre.assessment.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gre.assessment.Models.CurrencyConvert;
import com.gre.assessment.Models.CurrencyConvertListedItem;
import com.gre.assessment.Models.CurrencyHistory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EUCentralBankAPIConnectorUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public CurrencyConvert getLatestExchangeRate(String fromCurrency, String toCurrency) {
        ASyncHttpsTask task = new ASyncHttpsTask();
        task.setRequestHeaders("https://api.exchangeratesapi.io/latest?base=" +
                CountryCodeUtil.nameToCC(fromCurrency) + "&symbols=" +
                CountryCodeUtil.nameToCC(fromCurrency) + "," +
                CountryCodeUtil.nameToCC(toCurrency), "GET");
        try {
            task.execute().get();
            JsonNode resultJson = mapper.readTree(task.getResult());
            return CurrencyConvert.builder()
                    .fromCurrency(fromCurrency)
                    .toCurrency(toCurrency)
                    .fromCurrencyCode(CountryCodeUtil.nameToCC(fromCurrency))
                    .toCurrencyCode(CountryCodeUtil.nameToCC(toCurrency))
                    .fromRate(Double.valueOf(resultJson.get("rates").get(CountryCodeUtil.nameToCC(fromCurrency)).asText()))
                    .toRate(Double.valueOf(resultJson.get("rates").get(CountryCodeUtil.nameToCC(toCurrency)).asText()))
                    .build();
        } catch (ExecutionException | InterruptedException e) {
            return new CurrencyConvert("IO Error when Connecting to EU CB API");
        } catch (JsonProcessingException e) {
            return new CurrencyConvert("Error when Reading Response from EU CB API");
        }
    }

    public CurrencyHistory getHistoricalExchangeRate(String dateBegin, String dateEnd, String fromCurrency, String toCurrency) {
        String url = "https://api.exchangeratesapi.io/history?start_at=" + dateBegin + "&end_at=" + dateEnd + "&symbols=" +
                CountryCodeUtil.nameToCC(fromCurrency)  + "," +
                CountryCodeUtil.nameToCC(toCurrency) + "&base=" +
                CountryCodeUtil.nameToCC(fromCurrency);
        ASyncHttpsTask task = new ASyncHttpsTask();
        task.setRequestHeaders(url, "GET");
        try {
            task.execute().get();
            final JsonNode resultJson = mapper.readTree(task.getResult());
            final JsonNode ratesJson = resultJson.path("rates");
            List<CurrencyConvertListedItem> rates = new ArrayList<>();
            for (Iterator<Map.Entry<String, JsonNode>> it = ratesJson.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> localNode = it.next();
                final CurrencyConvertListedItem currencyConvert = CurrencyConvertListedItem.builder()
                        .fromRate(Double.valueOf(localNode.getValue().get(CountryCodeUtil.nameToCC(fromCurrency)).asText()))
                        .toRate(Double.valueOf(localNode.getValue().get(CountryCodeUtil.nameToCC(toCurrency)).asText()))
                        .date(localNode.getKey())
                        .build();
                rates.add(currencyConvert);
            }
            return CurrencyHistory.builder()
                    .currencyRates(rates)
                    .fromCurrency(fromCurrency)
                    .toCurrency(toCurrency)
                    .fromCurrencyCode(CountryCodeUtil.nameToCC(fromCurrency))
                    .toCurrencyCode(CountryCodeUtil.nameToCC(toCurrency))
                    .startDate(dateBegin)
                    .endDate(dateEnd)
                    .build();
        } catch (ExecutionException | InterruptedException e) {
            return new CurrencyHistory("IO Error when Connecting to EU CB API");
        } catch (JsonProcessingException e) {
            return new CurrencyHistory("Error when Reading Response from EU CB API");
        }
    }
}
