package starmeet.convergentmobile.com.starmeet.Models;

public class ChatMessage {
    public boolean isMy;
    public String message;
    public String id;
    public String avatarUrl;
    public int refId;
    public boolean isDevilry;

    public ChatMessage(boolean isMy, String message, String id, String avatarUrl, int refId) {
        super();
        this.isMy = isMy;
        this.message = message;
        this.avatarUrl = avatarUrl;
        this.id = id;
        this.refId = refId;
        isDevilry = refId !=0 && !id.isEmpty();
    }
}