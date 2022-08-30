package starmeet.convergentmobile.com.starmeet.WebRtc;

import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.voiceengine.WebRtcAudioUtils;

public class MyAudioDeviceModule implements AudioDeviceModule {
    public MyAudioDeviceModule(){
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false);
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(false);
    }

    @Override
    public long getNativeAudioDeviceModulePointer() {
        return 0;
    }

    @Override
    public void release() {

    }

    @Override
    public void setSpeakerMute(boolean b) {

    }

    @Override
    public void setMicrophoneMute(boolean b) {

    }
}
