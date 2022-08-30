package starmeet.convergentmobile.com.starmeet.WebRtc;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

public class SocketModel {

    @SerializedName("type")
    public TypeMessage type;

    @SerializedName("token")
    public String token;

    public String sdp;

    public Integer label;

    public String id;

    public String candidate;

    public String message;

    public int refId;

    @JsonAdapter(TypeMessage.Serializer.class)
    public enum TypeMessage {
        auth("a2s_authorize"),
        authSuccess("s2a_authorize_success"),
        authFail("s2a_authorize_fail"),
        doubleConnection("s2a_double_connection"),
        mediaReady("a2s_user_media_ready"),
        interlocutor("s2a_wait_interlocutor"),
        readyWaitStart("s2u_ready_wait_start"),
        readyToStart("s2c_ready_to_start"),
        callUser("c2s_calling"),
        callCelebrity("s2u_calling"),
        callRejectCelebrity("s2c_calling_reject"),
        callRejectUser("u2s_calling_reject"),
        callAcceptUser("s2c_calling_accept"),
        callAcceptCelebrity("u2s_calling_accept"),
        interlocutorLost("s2a_interlocutor_lost"),
        interlocutorLostApp("a2s_interlocutor_lost"),
        callEnded("s2a_call_ended"),
        callEnd("a2s_call_end"),
        candidate("candidate"),
        answer("answer"),
        offer("offer"),
        newMessageUser("a2s_text_message"),
        newMessageCelebrity("s2a_text_message"),
        confirmMessageCelebrity("s2a_text_message_confirm"),
        confirmMessageUser("a2s_text_message_confirm"),
        none("");

        private final String text;

        TypeMessage(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        private static TypeMessage getTypeMessageByText(String text) {
            for (TypeMessage typeMessage : values())
                if (typeMessage.toString().equals(text))
                    return typeMessage;

            return none;
        }

        private static class Serializer implements JsonSerializer<TypeMessage>, JsonDeserializer<TypeMessage> {
            @Override
            public JsonElement serialize(TypeMessage src, Type typeOfSrc, JsonSerializationContext context) {
                return context.serialize(src.text);
            }

            @Override
            public TypeMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                try {
                    return TypeMessage.getTypeMessageByText(json.getAsString());
                } catch (JsonParseException e) {
                    return TypeMessage.callEnd;
                }
            }
        }
    }
}
