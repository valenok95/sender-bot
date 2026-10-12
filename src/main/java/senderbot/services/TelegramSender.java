package senderbot.services;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import senderbot.configuration.BotConfiguration;

@Service
@Slf4j
public class TelegramSender extends AbilityBot {
    @Value("${ru.wallentos.sender-bot.admin-list}")
    public List<String> adminList;
    @Autowired
    private CacheService cacheService;

    protected TelegramSender(BotConfiguration botConfiguration) {
        super(botConfiguration.getKey(), botConfiguration.getName());
        log.info("BOT INITIALIZATION! ");
    }

    @Override
    public long creatorId() {
        return 0;
    }

    // === ABILITY: /forward — пересылает сообщение в канал ===
    public Ability forwardToChannel() {
        return Ability.builder()
                .name("forward")
                .info("Переслать сообщение в канал")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String userName = ctx.user().getUserName();
                    if (adminList.stream().noneMatch(userName::equalsIgnoreCase)) {
                        silent.send("Доступ к функционалу ограничен", ctx.chatId());
                        return;
                    }

                    String[] args = ctx.arguments();
                    if (args.length < 1) {
                        silent.send("❌ Использование: /forward <новый текст>", ctx.chatId());
                        return;
                    }
                    String text = String.join(" ", args);
                    cacheService.saveTextData(text);

                    List<String> channels = cacheService.getAllChats();
                    channels.forEach(channel -> {

                        // Копируем сообщение в канал
                        SendMessage sendMessage = SendMessage.builder()
                                .chatId(channel)
                                .text(text)
                                .build();

                        Message message = silent.execute(sendMessage).orElseThrow();
                        Long messageId = Long.valueOf(message.getMessageId());
                        silent.send("✅ Сообщение переслано в канал!", ctx.chatId());
                        cacheService.saveChatToMessageEntry(sendMessage.getChatId(), messageId);

                        // Закрепляем сообщение
                        PinChatMessage pin = new PinChatMessage();
                        pin.setChatId(channel);
                        pin.setMessageId(messageId.intValue());
                        pin.setDisableNotification(false);
                        silent.execute(pin);
                        silent.send("📌 Сообщение закреплено!", ctx.chatId());
                    });
                })
                .build();
    }

    // === ABILITY: /updatepost — обновляет (редактирует) сообщение в канале ===
    public Ability updatePost() {
        return Ability.builder()
                .name("updatepost")
                .info("Обновить сообщение в канале. Используйте: /updatepost <новый текст>")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String userName = ctx.user().getUserName();
                    if (adminList.stream().noneMatch(userName::equalsIgnoreCase)) {
                        silent.send("Доступ к функционалу ограничен", ctx.chatId());
                        return;
                    }
                    
                    String[] args = ctx.arguments();
                    if (args.length < 1) {
                        silent.send("❌ Использование: /updatepost <новый текст>", ctx.chatId());
                        return;
                    }
                    String text = String.join(" ", args);
                    updatePosts(text);
                })
                .build();
    }

    public void updatePosts(String newText) {
        cacheService.saveTextData(newText);
        Map<String, Long> chatsMap = cacheService.getAllChatsToMessageEntries();
        for (Map.Entry<String, Long> entry : chatsMap.entrySet()) {
            EditMessageText updateMessage = EditMessageText.builder()
                    .chatId(entry.getKey())
                    .messageId(entry.getValue().intValue())
                    .text(newText)
                    .build();
            silent.execute(updateMessage);
            log.info("Обновлено сообщение в чате {} с айдишником {} , новый текст: {}",
                    entry.getKey(), entry.getValue(), newText);
        }
    }

    public Ability replyToStart() {
        return Ability
                .builder()
                .name("start")
                .info("text")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {

                    String userName = ctx.user().getUserName();
                    if (adminList.stream().noneMatch(userName::equalsIgnoreCase)) {
                        silent.send("Доступ к функционалу ограничен", ctx.chatId());
                        return;
                    }
                    // запускаем команду forward для целевого значения (textMessageSource) + 
                    // сохраняем в базу пересланное

                    // Копируем сообщение в канал
                    SendMessage sendMessage = SendMessage.builder()
                            .chatId(ctx.chatId())
                            .text(cacheService.getTextData())
                            .build();

                    Message message = silent.execute(sendMessage).orElseThrow();
                    Long messageId = Long.valueOf(message.getMessageId());
                    cacheService.saveChatToMessageEntry(sendMessage.getChatId(), messageId);

                    // Закрепляем сообщение
                    PinChatMessage pin = new PinChatMessage();
                    pin.setChatId(ctx.chatId());
                    pin.setMessageId(messageId.intValue());
                    pin.setDisableNotification(false);
                    silent.execute(pin);
                })
                .build();
    }

    public Ability deleteProcess() {
        return Ability
                .builder()
                .name("delete")
                .info("text")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> {

                    String userName = ctx.user().getUserName();
                    if (adminList.stream().noneMatch(userName::equalsIgnoreCase)) {
                        silent.send("Доступ к функционалу ограничен", ctx.chatId());
                        return;
                    }
                    cacheService.deleteByChatId(String.valueOf(ctx.chatId()));
                })
                .build();
    }
}
