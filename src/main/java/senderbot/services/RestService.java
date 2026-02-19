package senderbot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Data
public class RestService {
    private static final String NAVER_HOST = "https://finance.naver.com/marketindex/exchangeDetail.nhn?marketindexCd=FX_USDKRW";
    private static final String UPBIT_HOST = "https://crix-api-cdn.upbit.com/v1/crix/trades/days?code=CRIX.UPBIT.KRW-USDT&count=2&convertingPriceUnit=KRW";

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
            var document = Jsoup.connect(NAVER_HOST).get();
            String rawResult =
                    document.selectFirst("th.th_ex5 + td").childNode(0).toString().replace(",", "");

            return stringToDownInt(rawResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
