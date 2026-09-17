package senderbot.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class RestService {
    private static final String NAVER_HOST = "https://stock.naver.com/api/securityService/marketindex/exchange/banksExchanges?bankType=HNB";
    private static final String UPBIT_HOST = "https://crix-api-cdn.upbit.com/v1/crix/trades/days?code=CRIX.UPBIT.KRW-USDT&count=2&convertingPriceUnit=KRW";
    private static final String CBR_HOST = "https://www.cbr-xml-daily.ru/daily_json.js";
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    @Autowired
    public RestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        mapper = new ObjectMapper();
    }


    /**
     * Получить курс SWIFT курс из naver.
     *
     * @return
     */
    public int getNaverRate() {
        try {
            String rawResult = Jsoup.connect(NAVER_HOST).ignoreContentType(true)
                    .execute()
                    .body();
            JsonNode root = mapper.readTree(rawResult);
            

            String value = extractCalcPrice(root);
      

            return stringToDownInt(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Извлечь данные из json.
     *
     * @param root
     * @return
     */
    private String extractCalcPrice(JsonNode root) {
        var elements = root.elements();
        while (elements.hasNext()) {
            var element = elements.next();
            if ("FX_USDKRW".equals(element.get("reutersCode").asText())) {
                return element.get("calcPrice").asText();
            }
        }
        return "";
    }


    /**
     * Получить курс CASH курс из upbit.
     *
     * @return
     */
    public int getUpBitRate() {
        try {
            String jsonResult =
                    Jsoup.connect(UPBIT_HOST).ignoreContentType(true).get().childNode(0).childNode(1).childNode(0).toString();
            String rawResult = mapper.readTree(jsonResult).get(0).get("tradePrice").asText();
            return stringToDownInt(rawResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить курс валют из ЦБ.
     *
     * @return
     */
    public Map<String, BigDecimal> getCbrRates() {
        try {
            Map<String, BigDecimal> resultMap = new HashMap();
            String jsonResult =
                    Jsoup.connect(CBR_HOST).ignoreContentType(true).get().childNode(0).childNode(1).childNode(0).toString();
            mapper.readTree(jsonResult).get("Valute").forEach(value -> {
                resultMap.put(value.get("CharCode").toString(), value.get("Value").decimalValue());
            });

            return resultMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить курс CASH курс из upbit.
     *
     * @return
     */
//    public int getCbrCnyRate() {
//        try {
//            String jsonResult =
//                    Jsoup.connect(CBR_HOST).ignoreContentType(true).get().childNode(0).childNode(1).childNode(0).toString();
//            String rawResult =
//                    mapper.readTree(jsonResult).get("Valute").get("CNY").get("Value").asText();
//            return stringToDownInt(rawResult);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    /**
     * Преобразовать в целое число с округлением вниз.
     *
     * @param string число в строке.
     * @return целое число.
     */
    private int stringToDownInt(String string) {

        BigDecimal original = new BigDecimal(string);

        BigDecimal rounded = original.setScale(0, RoundingMode.DOWN);

        return rounded.intValue();
    }

    /**
     * Преобразовать в целое число с округлением вниз.
     *
     * @param string число в строке.
     * @return целое число.
     */
    private int stringToBigDecimal(String string) {

        BigDecimal original = new BigDecimal(string);

        BigDecimal rounded = original.setScale(0, RoundingMode.DOWN);

        return rounded.intValue();
    }
}
