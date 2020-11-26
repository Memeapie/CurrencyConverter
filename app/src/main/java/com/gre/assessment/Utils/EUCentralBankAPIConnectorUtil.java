package com.gre.assessment.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gre.assessment.Models.CurrencyConvert;

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

    public String getHistoricalExchangeRate(String dateBegin, String dateEnd, String fromCurrency, String toCurrency) {
        ASyncHttpsTask task = new ASyncHttpsTask();
        task.setRequestHeaders("https://api.exchangeratesapi.io/history?start_at=" + dateBegin + "&end_at=" + dateEnd + "&base=" +
                CountryCodeUtil.nameToCC(fromCurrency)  + "&symbols=" +
                CountryCodeUtil.nameToCC(fromCurrency)  + "," +
                CountryCodeUtil.nameToCC(toCurrency) , "GET");
        try {
            task.execute().get();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode resultJson = mapper.readTree(task.getResult());
        } catch (ExecutionException | InterruptedException | JsonProcessingException e) {
            return "Could not connect to API";
        }
        return task.getResult();
    }
}
