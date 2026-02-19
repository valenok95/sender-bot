package senderbot.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderService {
    /**
     * Отнимаем от курса cash naver коррекцию.
     */
    @Value("${ru.wallentos.sender-bot.minus-cash-correction}")
    private int minusCashCorrection;

    /**
     * Отнимаем от swift upbit коррекцию.
     */
    @Value("${ru.wallentos.sender-bot.minus-swift-correction}")
    private int minusSwiftCorrection;

    @Autowired
    private RestService restService;

    @Autowired
    private TelegarmSender telegarmSender;

    /**
     * Обновить сообщение по всем чатам.
     */
    public void updatePosts(String newText) {
        telegarmSender.updatePosts(newText);
    }

    /**
     * Процесс обновления постов.
     */
    public void updateRatesProcess() {
        int cashRate = restService.getUpBitRate() - minusCashCorrection;
        int swiftRate = restService.getNaverRate() - minusSwiftCorrection;

        String preparedMessage = prepareMessage(cashRate, swiftRate);
        telegarmSender.updatePosts(preparedMessage);

    }

    private String prepareMessage(int cashRate, int swiftRate) {
        return String.format("""
                        🔴 CASH %d
                        🟢 SWIFT %d
                        Обновлено: %s
                        """, cashRate, swiftRate,
                LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DateTimeFormatter.ofPattern("dd MMMM в HH:mm").localizedBy(Locale.forLanguageTag("ru"))));
    }
}