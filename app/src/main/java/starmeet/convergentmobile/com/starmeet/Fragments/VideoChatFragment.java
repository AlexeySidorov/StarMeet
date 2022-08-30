package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaCodecVideoEncoder;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCodecInfo;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.voiceengine.WebRtcAudioTrack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import starmeet.convergentmobile.com.starmeet.Activites.VideoInitChatActivity;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackTimer;
import starmeet.convergentmobile.com.starmeet.Callbaks.DialogCallback;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Helpers.ChatHelper;
import starmeet.convergentmobile.com.starmeet.Models.IceServer;
import starmeet.convergentmobile.com.starmeet.MyContext;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Services.DialogService;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;
import starmeet.convergentmobile.com.starmeet.WebRtc.CustomPeerConnectionObserver;
import starmeet.convergentmobile.com.starmeet.WebRtc.CustomSdpObserver;
import starmeet.convergentmobile.com.starmeet.WebRtc.LooperExecutor;
import starmeet.convergentmobile.com.starmeet.WebRtc.MyAudioDeviceModule;
import starmeet.convergentmobile.com.starmeet.WebRtc.ProxyVideoSink;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingChatInterface;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingClient;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingInterface;
import starmeet.convergentmobile.com.starmeet.WebRtc.SocketModel;


public class VideoChatFragment extends BaseFragment implements SignalingInterface, SignalingChatInterface,
        CallbackEmptyFunc, CallbackTimer, View.OnClickListener {
    PeerConnectionFactory peerConnectionFactory;
    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    private ProxyVideoSink remoteProxyRenderer = new ProxyVideoSink();
    private final ProxyVideoSink localProxyVideoSink = new ProxyVideoSink();
    @Nullable
    private SurfaceViewRenderer pipRenderer;
    @Nullable
    private SurfaceViewRenderer fullscreenRenderer;
    PeerConnection localPeer;
    private EglBase rootEglBase;
    private MediaConstraints sdpMediaConstraints;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    private LooperExecutor executor;
    private String TAG = "VideoChat";
    private ActivityCommunicator<Integer> activityCommunicator;
    private ArrayList<IceServer> iceServers;
    private AppCompatTextView timeChat;
    private AppCompatImageButton stopConnection;
    private AppCompatImageButton videoPause;
    private AppCompatImageButton muteAudio;
    private AppCompatImageButton muteMicrophone;
    private AppCompatImageButton changeCamera;
    private MediaStream mediaStream;
    private VideoCapturer videoCapturedAndroid;
    private AudioTrack remoteAudioTrack;
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    private SurfaceTextureHelper surfaceTextureHelper;
    private VideoCodecInfo[] vc;
    private VideoTrack videoTrack;
    private SurfaceHolder holder;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_videochat, null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            activityCommunicator = (ActivityCommunicator<Integer>) context;
        } catch (Exception exp) {
            LogUtil.logE("VideoChatFragment", exp.toString());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setBackgroundToolbar(R.color.transparent);
        initViews();
        initData();
        initClickListener();
        settingsControls();
        setIconChat(R.drawable.group465, this);
        ChatHelper.getInstance().startChat();
        ChatHelper.getInstance().setListener(this);
        SignalingClient.getInstance().setCallbackVideoChat(this);
        SignalingClient.getInstance().setCallbackChat(this);

        isChangeIcon = false;
    }

    private void initViews() {
        pipRenderer = Objects.requireNonNull(getActivity()).findViewById(R.id.pip_video_view);
        fullscreenRenderer = Objects.requireNonNull(getActivity().findViewById(R.id.fullscreen_video_view));
        timeChat = Objects.requireNonNull(getActivity()).findViewById(R.id.time_chat);
        stopConnection = Objects.requireNonNull(getActivity()).findViewById(R.id.stop_connection);
        muteAudio = Objects.requireNonNull(getActivity()).findViewById(R.id.mute_audio);
        videoPause = Objects.requireNonNull(getActivity()).findViewById(R.id.stop_video);
        muteMicrophone = Objects.requireNonNull(getActivity()).findViewById(R.id.mute_microphone);
        changeCamera = Objects.requireNonNull(getActivity()).findViewById(R.id.change_camera);


    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            activityCommunicator.passDataToActivity(0, -1);
            return;
        }

        String urls = bundle.getString("ice_server_urls");
        if (urls == null) {
            activityCommunicator.passDataToActivity(0, -1);
            return;
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<IceServer>>() {
        }.getType();
        iceServers = gson.fromJson(urls, listType);
    }

    private void initClickListener() {
        stopConnection.setOnClickListener(this);
        muteAudio.setOnClickListener(this);
        videoPause.setOnClickListener(this);
        muteMicrophone.setOnClickListener(this);
        changeCamera.setOnClickListener(this);
    }

    private void settingsControls() {
        if (rootEglBase == null)
            rootEglBase = EglBase.create();

        Objects.requireNonNull(fullscreenRenderer).init(rootEglBase.getEglBaseContext(), null);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        fullscreenRenderer.setEnableHardwareScaler(false);
        fullscreenRenderer.setFpsReduction(30);
        fullscreenRenderer.setKeepScreenOn(true);
        fullscreenRenderer.setMirror(true);

        Objects.requireNonNull(pipRenderer).setZOrderMediaOverlay(true);
        assert pipRenderer != null;
        pipRenderer.init(rootEglBase.getEglBaseContext(), null);
        pipRenderer.setEnableHardwareScaler(false);
        pipRenderer.setFpsReduction(30);
        pipRenderer.setKeepScreenOn(true);

        localProxyVideoSink.setTarget(pipRenderer);
        remoteProxyRenderer.setTarget(fullscreenRenderer);
    }

    public void createVideoConstraints(MediaConstraints videoConstraints) {
        String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
        String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
        String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
        String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
        String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
        String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";

        int videoWidth = 0;
        int videoHeight = 0;

        if (MediaCodecVideoEncoder.isVp8HwSupported()) {
            videoWidth = 360;
            videoHeight = 480;
        }

        if (videoWidth > 0) {
            videoWidth = Math.min(videoWidth, 360);
            videoHeight = Math.min(videoHeight, 480);
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(videoWidth)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
            videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(videoHeight)));
        }

        int videoFps = 24;

        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(videoFps)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(videoFps)));
    }

    public void createAudioConstraints(MediaConstraints pcConstraints) {
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("RtpDataChannels", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("internalSctpDataChannels", "true"));
    }

    public void createSdpMediaConstraints(MediaConstraints sdpMediaConstraints) {
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
    }

    @Override
    public void onStart() {
        super.onStart();
        initDevice();
    }

    @Override
    public void onOfferReceived(SocketModel model) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (localPeer != null) {
                    SessionDescription sdp = getNewSessionDescription(new SessionDescription(SessionDescription.Type.OFFER, model.sdp));
                    localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), sdp);
                }
            }
        });
    }

    @Override
    public void onAnswerReceived(SocketModel model) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (localPeer != null) {
                    SessionDescription sdp = getNewSessionDescription(new SessionDescription(SessionDescription.Type.ANSWER, model.sdp));
                    localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), sdp);
                }
            }
        });
    }

    private void answer() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                localPeer.createAnswer(new CustomSdpObserver("localCreateAns") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);

                        SessionDescription sdp = getNewSessionDescription(sessionDescription);
                        localPeer.setLocalDescription(new CustomSdpObserver("localSetLocal"), sdp);
                        SocketModel model = new SocketModel();
                        model.type = SocketModel.TypeMessage.answer;
                        model.sdp = sdp.description;
                        SignalingClient.getInstance().sendMessage(model);
                    }
                }, sdpMediaConstraints);
            }
        });
    }

    private void createOffer() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                localPeer.createOffer(new CustomSdpObserver("localCreateOffer") {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {
                        super.onCreateSuccess(sessionDescription);

                        SessionDescription sdp = getNewSessionDescription(sessionDescription);
                        localPeer.setLocalDescription(new CustomSdpObserver("localSetLocal"), sdp);
                        SocketModel model = new SocketModel();
                        model.type = SocketModel.TypeMessage.offer;
                        model.sdp = sdp.description;
                        SignalingClient.getInstance().sendMessage(model);
                    }
                }, sdpMediaConstraints);
            }
        });
    }

    private SessionDescription getNewSessionDescription(SessionDescription sessionDescription) {
        String sdpDescription = sessionDescription.description;
        sdpDescription = preferCodec(sdpDescription, "ISAC", true);
        sdpDescription = preferCodec(sdpDescription, "H264", false);
        return new SessionDescription(sessionDescription.type, sdpDescription);
    }

    @Override
    public void onIceCandidateReceived(SocketModel model) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                localPeer.addIceCandidate(new IceCandidate(model.id, model.label,
                        model.candidate == null ? model.sdp : model.candidate));
            }
        });
    }

    private boolean isChangeIcon = false;

    @Override
    public void onNewMessage(SocketModel model) {
        if (!isChangeIcon) {
            isChangeIcon = true;
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setIconChat(R.drawable.group466, new CallbackEmptyFunc() {
                        @Override
                        public void Click() {
                            activityCommunicator.passDataToActivity(0, 2);
                        }
                    });
                }
            });
        }

        ChatHelper.getInstance().setMessage(model);
    }

    @Override
    public void onConfirm(SocketModel model) {

    }

    @Override
    public void onClose() {
        closePeerToPeer();
    }

    private void closePeerToPeer() {
        if (isStart) {
            localPeer.close();
            executor.requestStop();
            mediaStream.dispose();
            videoCapturedAndroid.dispose();
            rootEglBase.release();
            Objects.requireNonNull(pipRenderer).release();
            Objects.requireNonNull(fullscreenRenderer).release();
            isStart = false;

            ChatHelper.getInstance().stopChat();
            Intent intent = new Intent(getActivity(), VideoInitChatActivity.class);
            Objects.requireNonNull(getActivity()).startActivity(intent);
        }
    }

    private boolean isStart = false;

    @SuppressWarnings({"MoveFieldAssignmentToInitializer", "deprecation"})
    public void initDevice() {

        if (isStart) return;
        isStart = true;

        videoConstraints = new MediaConstraints();
        audioConstraints = new MediaConstraints();
        sdpMediaConstraints = new MediaConstraints();

        createVideoConstraints(videoConstraints);
        createSdpMediaConstraints(sdpMediaConstraints);
        createAudioConstraints(audioConstraints);

        executor = new LooperExecutor();
        executor.requestStart();

        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(getActivity())
                        .setEnableInternalTracer(true)
                        .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        VideoEncoderFactory v = new SoftwareVideoEncoderFactory();
        vc = v.getSupportedCodecs();

        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());

        PeerConnectionFactory.Builder peerConnectionFactoryBuilder = PeerConnectionFactory.builder();
        peerConnectionFactoryBuilder.setAudioDeviceModule(new MyAudioDeviceModule());
        peerConnectionFactoryBuilder.setVideoDecoderFactory(defaultVideoDecoderFactory);
        peerConnectionFactoryBuilder.setVideoEncoderFactory(v);
        peerConnectionFactoryBuilder.setOptions(options);
        peerConnectionFactory = peerConnectionFactoryBuilder.createPeerConnectionFactory();

        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        videoCapturedAndroid = createCameraCapture(new Camera1Enumerator(false));
        assert videoCapturedAndroid != null;
        createVideoTrack(videoCapturedAndroid);
        createPeerConnection();
        settingsAudioDevice();
    }

    private void settingsAudioDevice() {
        AudioManager audioManager = (AudioManager) Objects.requireNonNull(getActivity()).getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        audioManager.setSpeakerphoneOn(true);
    }

    private void createPeerConnection() {
        if (MyContext.getInstance().userRole == 1) {
            SocketModel model = new SocketModel();
            model.type = SocketModel.TypeMessage.callAcceptCelebrity;
            SignalingClient.getInstance().sendMessage(model);
        }

        List<PeerConnection.IceServer> serversUrl = new ArrayList<>();
        for (IceServer server : iceServers) {
            PeerConnection.IceServer.Builder iceServer = PeerConnection.IceServer.builder(server.urls);
            iceServer.setUsername(server.username);
            iceServer.setPassword(server.credential);
            serversUrl.add(iceServer.createIceServer());
        }

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(serversUrl);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.BALANCED;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;


        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                SocketModel model = new SocketModel();
                model.type = SocketModel.TypeMessage.candidate;
                model.sdp = iceCandidate.sdp;
                model.label = iceCandidate.sdpMLineIndex;
                model.id = iceCandidate.sdpMid;
                SignalingClient.getInstance().sendMessage(model);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                LogUtil.logD("Stream", "Received Remote stream");
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                super.onIceGatheringChange(iceGatheringState);
                LogUtil.logD("Stream", iceGatheringState.toString());
            }
        });

        addStreamToLocalPeer();
    }

    @Nullable
    private VideoTrack createVideoTrack(VideoCapturer capture) {
        surfaceTextureHelper =
                SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
        videoSource = peerConnectionFactory.createVideoSource(capture.isScreencast());
        capture.initialize(surfaceTextureHelper, getActivity(), videoSource.getCapturerObserver());
        capture.startCapture(200, 200, 24);

        localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(true);
        localVideoTrack.addSink(localProxyVideoSink);
        return localVideoTrack;
    }

    private void addStreamToLocalPeer() {
        mediaStream = peerConnectionFactory.createLocalMediaStream("ARDAMS");
        mediaStream.addTrack(localAudioTrack);
        mediaStream.addTrack(localVideoTrack);
        localPeer.addStream(mediaStream);

        if (MyContext.getInstance().userRole == 2)
            createOffer();
    }

    private MediaStream remoteStream;

    @SuppressWarnings("deprecation")
    private void gotRemoteStream(final MediaStream stream) {
        remoteStream = stream;

        if (stream.videoTracks.size() > 0) {
            videoTrack = stream.videoTracks.get(0);
            videoTrack.setEnabled(true);
            videoTrack.addSink(remoteProxyRenderer);
        }

        if (stream.audioTracks.size() > 0) {
            WebRtcAudioTrack.setAudioTrackUsageAttribute(AudioAttributes.USAGE_MEDIA);
            remoteAudioTrack = stream.audioTracks.get(0);
        }

        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (MyContext.getInstance().userRole == 1)
                        answer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        LogUtil.logD(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    @Override
    public void Click() {
        activityCommunicator.passDataToActivity(0, 2);
    }

    @Override
    public void Value(final String time) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timeChat.setText(time);
            }
        });
    }

    private boolean isEnabledVideo = true;
    private boolean isEnabledMicrophone = true;
    private boolean isEnabledAudio = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stop_connection: {
                DialogService.MessageDialog(Objects.requireNonNull(getActivity()),
                        "Call", "Do you want to end the call?", "Yes, end the call",
                        new DialogCallback<DialogService.ButtonType>() {
                            @Override
                            public void onResult(DialogService.ButtonType result) {
                                SignalingClient.getInstance().closeConnection();
                            }

                            @Override
                            public void onClose() {

                            }
                        });
                break;
            }
            case R.id.mute_audio: {
                if (remoteAudioTrack == null) return;

                isEnabledAudio = !isEnabledAudio;
                muteAudio.setImageResource(isEnabledAudio ? R.drawable.group758 : R.drawable.group483);
                remoteAudioTrack.setEnabled(isEnabledAudio);
                break;
            }
            case R.id.mute_microphone: {
                isEnabledMicrophone = !isEnabledMicrophone;
                muteMicrophone.setImageResource(isEnabledMicrophone ? R.drawable.group756 : R.drawable.group485);
                localAudioTrack.setEnabled(isEnabledMicrophone);
                break;
            }
            case R.id.stop_video: {
                isEnabledVideo = !isEnabledVideo;
                videoPause.setImageResource(isEnabledVideo ? R.drawable.group755 : R.drawable.group486);
                localVideoTrack.setEnabled(isEnabledVideo);
                break;
            }
            case R.id.change_camera: {
                switchCamera();
                break;
            }
        }
    }

    private void switchCamera() {
        if (videoCapturedAndroid != null) {
            if (videoCapturedAndroid instanceof CameraVideoCapturer) {
                CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoCapturedAndroid;
                cameraVideoCapturer.switchCamera(null);
            }
        }
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
