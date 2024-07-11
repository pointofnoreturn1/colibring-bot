package io.vaku.model;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class ClassifiedUpdate {

    private final Update update;

    private final MessageType messageType;

    private final long userId;

    private final long chatId;

    private final String userName;

    private final String firstName;

    private final String lastName;

    private final String commandName;

    public ClassifiedUpdate(Update update) {
        this.update = update;
        this.messageType = handleMessageType();
        this.userId = handleUserId();
        this.chatId = handleChatId();
        this.userName = handleUserName();
        this.firstName = handleFirstName();
        this.lastName = handleLastName();
        this.commandName = handleCommand();
    }

    public String handleCommand() {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }

        return "";
    }

    private MessageType handleMessageType() {
        if (update.hasMessage() && update.getMessage().hasText()) {
            return MessageType.TEXT;
        }

        if (update.hasCallbackQuery()) {
            return MessageType.CALLBACK;
        }

        return MessageType.UNKNOWN;
    }

    private long handleUserId() {
        if (messageType == MessageType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            return update.getMessage().getFrom().getId();
        }
    }

    private long handleChatId() {
        if (messageType == MessageType.CALLBACK) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else {
            return update.getMessage().getChatId();
        }
    }

    private String handleUserName() {
        if (messageType == MessageType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getUserName();
        } else {
            return update.getMessage().getFrom().getUserName();
        }
    }

    private String handleFirstName() {
        if (messageType == MessageType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getFirstName();
        } else {
            return update.getMessage().getFrom().getFirstName();
        }
    }

    private String handleLastName() {
        if (messageType == MessageType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getLastName();
        } else {
            return update.getMessage().getFrom().getLastName();
        }
    }


}
