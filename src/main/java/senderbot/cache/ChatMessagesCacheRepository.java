package senderbot.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessagesCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String senderBotChatPrefix = "senderbot_chat_";
    private final String senderBotDataSourceKey = "senderbot_data";

    public void saveChatIdToMessageId(String chatId, Long messageId) {
        redisTemplate.opsForValue().set(senderBotChatPrefix + chatId, messageId);
    }

    public void saveTextData(String textData) {
        redisTemplate.opsForValue().set(senderBotDataSourceKey, textData);
    }

    public String getTextData() {
        return redisTemplate.opsForValue().get(senderBotDataSourceKey).toString();
    }


    public boolean hasChatId(String chatId) {
        return redisTemplate.opsForValue().getOperations().hasKey(senderBotChatPrefix + chatId);
    }

    private Long getMessageIdByChatId(String chatId) {
        Integer messageId = (Integer) redisTemplate.opsForValue().get(senderBotChatPrefix + chatId);
        return Long.parseLong(messageId + "");
    }

    public void deleteById(String chatId) {
        redisTemplate.delete(senderBotChatPrefix + chatId);
    }

    public List<String> getAllChatIds() {
        return Objects.requireNonNull(redisTemplate.keys(senderBotChatPrefix + "*")).stream().map(key -> key.replace(senderBotChatPrefix, "")).toList();
    }

    public Map<String, Long> getAllChatsToMessageEntries() {
        Map<String, Long> result = new HashMap<>();

        List<String> chatIds = getAllChatIds();
        chatIds.forEach(chatId -> {
                    Long messageId = getMessageIdByChatId(chatId);
                    result.put(chatId, messageId);
                }
        );
        return result;
    }
    
}
