package io.vaku.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;

@Data
@NoArgsConstructor
public class Response {
    private SendDocument sendDocument;
    private SendPhoto sendPhoto;
    private SendVideo sendVideo;
    private SendVideoNote sendVideoNote;
    private SendSticker sendSticker;
    private SendAudio sendAudio;
    private SendVoice sendVoice;
    private SendMediaGroup sendMediaGroup;
    private SetChatPhoto setChatPhoto;
    private AddStickerToSet addStickerToSet;
    private CreateNewStickerSet createNewStickerSet;
    private UploadStickerFile uploadStickerFile;
    private EditMessageMedia editMessageMedia;
    private SendAnimation sendAnimation;
    private BotApiMethod<?> botApiMethod;

    public Response(BotApiMethod<?> botApiMethod) {
        this.botApiMethod = botApiMethod;
    }

    public Response(SendMediaGroup sendMediaGroup) {
        this.sendMediaGroup = sendMediaGroup;
    }
}
