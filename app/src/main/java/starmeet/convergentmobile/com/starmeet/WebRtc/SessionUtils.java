package starmeet.convergentmobile.com.starmeet.WebRtc;

import android.support.annotation.Nullable;
import android.util.Log;

import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionUtils {

    public static SessionDescription getNewSessionDescription(SessionDescription sessionDescription) {
        String sdpDescription = sessionDescription.description;
        sdpDescription = preferCodec(sdpDescription, "ISAC", true);
        sdpDescription = preferCodec(sdpDescription, "H264", false);
        return new SessionDescription(sessionDescription.type, sdpDescription);
    }

    private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        final String[] lines = sdpDescription.split("\r\n");
        final int mLineIndex = findMediaDescriptionLine(isAudio, lines);
        if (mLineIndex == -1) {
            Log.w("v-codec", "No mediaDescription line, so can't prefer " + codec);
            return sdpDescription;
        }

        final List<String> codecPayloadTypes = new ArrayList<>();
        final Pattern codecPattern = Pattern.compile("^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$");
        for (String line : lines) {
            Matcher codecMatcher = codecPattern.matcher(line);
            if (codecMatcher.matches()) {
                codecPayloadTypes.add(codecMatcher.group(1));
            }
        }

        if (codecPayloadTypes.isEmpty()) {
            Log.w("v-codec", "No payload types with name " + codec);
            return sdpDescription;
        }

        final String newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]);
        if (newMLine == null) {
            return sdpDescription;
        }

        Log.d("v-codec", "Change media description from: " + lines[mLineIndex] + " to " + newMLine);
        lines[mLineIndex] = newMLine;
        return joinString(Arrays.asList(lines), "\r\n", true);
    }

    private static @Nullable
    String movePayloadTypesToFront(List<String> preferredPayloadTypes, String mLine) {
        final List<String> origLineParts = Arrays.asList(mLine.split(" "));

        if (origLineParts.size() <= 3) {
            Log.e("SDP media", "Wrong SDP media description format: " + mLine);
            return null;
        }

        final List<String> header = origLineParts.subList(0, 3);
        final List<String> unpreferredPayloadTypes =
                new ArrayList<>(origLineParts.subList(3, origLineParts.size()));
        unpreferredPayloadTypes.removeAll(preferredPayloadTypes);

        final List<String> newLineParts = new ArrayList<>();
        newLineParts.addAll(header);
        newLineParts.addAll(preferredPayloadTypes);
        newLineParts.addAll(unpreferredPayloadTypes);
        return joinString(newLineParts, " ", false);
    }

    private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
        final String mediaDescription = isAudio ? "m=audio " : "m=video ";
        for (int i = 0; i < sdpLines.length; ++i) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i;
            }
        }
        return -1;
    }

    private static String joinString(
            Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
        Iterator<? extends CharSequence> iter = s.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter);
        }
        return buffer.toString();
    }
}
