package senderbot.services;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import senderbot.cache.ChatMessagesCacheRepository;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final ChatMessagesCacheRepository repository;

    /**
     * Сохранить номер чата и сообщения в нём для дальнейшего контроля над сообщением.
     *
     * @param chatId
     * @param messageId
     */
    public void saveChatToMessageEntry(String chatId, Long messageId) {
        repository.saveChatIdToMessageId(chatId, messageId);
    }

    /***
     * Получить соотношение чатов и айдишников закрепленных сообщений для обновления рассылки.
     * @return
     */
    public Map<String, Long> getAllChatsToMessageEntries() {
        return repository.getAllChatsToMessageEntries();
    }

    /***
     * Получить соотношение чатов и айдишников закрепленных сообщений для обновления рассылки.
     * @return
     */
    public List<String> getAllChats() {
        return repository.getAllChatIds();
    }

    /***
     * Текст для рассылки.
     */
    public String getTextData() {
        return repository.getTextData();
    }

    /***
     * Текст для рассылки.
     */
    public void saveTextData(String text) {
        repository.saveTextData(text);
    }

    /**
     * Удаление чата из рассылки.
     *
     * @param chatId номер чата
     */
    public void deleteByChatId(String chatId) {
        repository.deleteById(chatId);
    }

}
