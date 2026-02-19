package senderbot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    private final SenderService senderService;

    /**
     * Задание на обновление КЭШа для мастер-бота.
     */
    @Scheduled(cron = "${ru.wallentos.sender-bot.update-posts-cron}")
    public void updateRates() {
        log.info("Updating rates");
        senderService.updateRatesProcess();
        log.info("Checking new transactions finished");
    }
}
