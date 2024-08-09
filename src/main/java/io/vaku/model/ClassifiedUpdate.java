package io.vaku.model;

import io.vaku.model.enm.TelegramType;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class ClassifiedUpdate {

    private final Update update;

    private final TelegramType handlerType;

    private final long userId;

    private final long chatId;

    private final String userName;

    private final String firstName;

    private final String lastName;

    private final String commandName;

    private final String photoFileId;

    public ClassifiedUpdate(Update update) {
        this.update = update;
        this.handlerType = handleTelegramType();
        this.userId = handleUserId();
        this.chatId = handleChatId();
        this.userName = handleUserName();
        this.firstName = handleFirstName();
        this.lastName = handleLastName();
        this.commandName = handleCommand();
        this.photoFileId = handlePhoto();
    }



    private String handleCommand() {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                return update.getMessage().getText().split(" ")[0];
            } else {
                return update.getMessage().getText();
            }
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }

        return "";
    }

    private TelegramType handleTelegramType() {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().startsWith("/")) {
                return TelegramType.COMMAND;
            } else {
                return TelegramType.TEXT;
            }
        }

        if (update.hasCallbackQuery()) {
            return TelegramType.CALLBACK;
        }

        return TelegramType.UNKNOWN;
    }

    private long handleUserId() {
        if (handlerType == TelegramType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            return update.getMessage().getFrom().getId();
        }
    }

    private long handleChatId() {
        if (handlerType == TelegramType.CALLBACK) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else {
            return update.getMessage().getChatId();
        }
    }

    private String handleUserName() {
        if (handlerType == TelegramType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getUserName();
        } else {
            return update.getMessage().getFrom().getUserName();
        }
    }

    private String handleFirstName() {
        if (handlerType == TelegramType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getFirstName();
        } else {
            return update.getMessage().getFrom().getFirstName();
        }
    }

    private String handleLastName() {
        if (handlerType == TelegramType.CALLBACK) {
            return update.getCallbackQuery().getFrom().getLastName();
        } else {
            return update.getMessage().getFrom().getLastName();
        }
    }

    private String handlePhoto() {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            return update.getMessage().getPhoto().getFirst().getFileId();
        }

        return null;
    }
}
