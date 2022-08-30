package starmeet.convergentmobile.com.starmeet.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import starmeet.convergentmobile.com.starmeet.Adapters.ChatArrayAdapter;
import starmeet.convergentmobile.com.starmeet.Base.BaseFragment;
import starmeet.convergentmobile.com.starmeet.Callbaks.CallbackEmptyFunc;
import starmeet.convergentmobile.com.starmeet.Communicators.ActivityCommunicator;
import starmeet.convergentmobile.com.starmeet.Helpers.ChatHelper;
import starmeet.convergentmobile.com.starmeet.Models.ChatMessage;
import starmeet.convergentmobile.com.starmeet.R;
import starmeet.convergentmobile.com.starmeet.Utils.LogUtil;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingChatInterface;
import starmeet.convergentmobile.com.starmeet.WebRtc.SignalingClient;
import starmeet.convergentmobile.com.starmeet.WebRtc.SocketModel;

public class ChatFragment extends BaseFragment implements View.OnClickListener, SignalingChatInterface,
        CallbackEmptyFunc {

    private RecyclerView chatList;
    //private AppCompatImageButton addMedia;
    private AppCompatImageButton sendMessage;
    private AppCompatEditText message;
    private ChatArrayAdapter chatArrayAdapter;
    private ActivityCommunicator<Integer> activityCommunicator;
    private String url;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat, null);
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
        setBackgroundToolbar(R.color.white);
        initViews();
        initData();
        initClick();
        chatInit();
        setIconChat(R.drawable.group491, this);
        SignalingClient.getInstance().setCallbackChat(this);
    }

    private void initViews() {
        chatList = Objects.requireNonNull(this.getActivity()).findViewById(R.id.chat_list);
        //addMedia = Objects.requireNonNull(this.getActivity()).findViewById(R.id.button_chat_box_add);
        sendMessage = Objects.requireNonNull(this.getActivity()).findViewById(R.id.button_chat_box_send);
        message = Objects.requireNonNull(this.getActivity()).findViewById(R.id.text_chat_box);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            activityCommunicator.passDataToActivity(0, -1);
            return;
        }

        String name = bundle.getString("NickName");
        url = bundle.getString("Avatar");
        if (name == null) {
            activityCommunicator.passDataToActivity(0, -1);
            return;
        }

        setTitleToolbar(name);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initClick() {
        //addMedia.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        //chatList.setOnTouchListener(new View.OnTouchListener() {
          //  @Override
          //  public boolean onTouch(View view, MotionEvent motionEvent) {
          //      message.hasFocus();
          //      InputMethodManager imm = (InputMethodManager)
          //              Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
          //      Objects.requireNonNull(imm).hideSoftInputFromWindow(message.getWindowToken(), 0);
          //      return false;
          //  }
      //  });
    }

    private void chatInit() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());

        List<ChatMessage> list = new ArrayList<>();
        for (SocketModel model : ChatHelper.getInstance().getHistory())
            list.add(new ChatMessage(model.refId != 0, model.message, model.id,
                    model.refId == 0 ? url : null, model.refId));

        chatArrayAdapter = new ChatArrayAdapter(getActivity(), list);
        chatList.setLayoutManager(layoutManager);
        chatList.setHasFixedSize(true);
        chatList.setAdapter(chatArrayAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_list:
                break;
            case R.id.button_chat_box_add:
                break;
            case R.id.button_chat_box_send:
                sendChatMessage();
                break;
        }
    }

    private void sendChatMessage() {
        String msg = Objects.requireNonNull(message.getText()).toString();
        if (msg.isEmpty()) return;

        SocketModel model = new SocketModel();
        model.type = SocketModel.TypeMessage.newMessageUser;
        model.message = msg;
        model.refId = chatArrayAdapter.getItemCount() == 0 ? 1 : chatArrayAdapter.getCountMyMessage() + 1;
        SignalingClient.getInstance().sendMessage(model);
        message.setText("");

        chatArrayAdapter.addNewMessage(new ChatMessage(true, msg, "", null, model.refId));
        chatList.smoothScrollToPosition(chatArrayAdapter.getItemCount() - 1);
        InputMethodManager imm = (InputMethodManager)
                Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(message.getWindowToken(), 0);
    }

    @Override
    public void onNewMessage(SocketModel model) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatArrayAdapter.addNewMessage(
                        new ChatMessage(false, model.message, model.id, url, model.refId));
            }
        });
    }

    @Override
    public void onConfirm(SocketModel model) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                model.message = chatArrayAdapter.setMyMessageId(model.refId, model.id);
                ChatHelper.getInstance().setMessage(model);
            }
        });
    }

    @Override
    public void onClose() {

    }

    @Override
    public void Click() {
        activityCommunicator.passDataToActivity(0, 1);
    }
}
