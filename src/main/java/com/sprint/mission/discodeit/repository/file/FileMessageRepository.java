package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.file.FileDataStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final FileDataStore<Message> store;
    private final Map<UUID, Message> data;

    public FileMessageRepository() {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();

        this.store = new FileDataStore<>("data/messages.ser");
        this.data = store.load();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        store.save(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message delete(Message message) {
        Message removed = data.remove(message.getId());
        store.save(data);
        return removed;
    }
}