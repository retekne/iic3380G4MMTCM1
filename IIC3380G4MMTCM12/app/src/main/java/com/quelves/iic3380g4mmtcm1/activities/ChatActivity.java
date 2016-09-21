package com.quelves.iic3380g4mmtcm1.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.quelves.iic3380g4mmtcm1.databinding.ActivityChatBinding;
import com.quelves.iic3380g4mmtcm1.model.ChatMessage;
import com.quelves.iic3380g4mmtcm1.model.ChatSettings;

import com.quelves.iic3380g4mmtcm1.BR;
import com.quelves.iic3380g4mmtcm1.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Activity {
    private static final String TAG = "Chat";
    private static final String KEY_SETTINGS = "settings";
    private static final String FIREBASE_KEY_ROOMS = "rooms";

    private ActivityChatBinding mBinding;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatRoomReference;

    private ChatSettings mChatSettings;
    private boolean mInitialized;

    private List<ChatMessage> mMessageList;
    private ArrayAdapter<ChatMessage> mAdapter;

    // Inner classes implementations

    public class ChatActivityHandler {
        public void onSendMessage(TextHolder textHolder) {
            if (mInitialized) {
                ChatMessage newMessage = new ChatMessage(mChatSettings.getUsername(), textHolder.getText());

                mChatRoomReference.push().setValue(newMessage);
                mAdapter.add(newMessage);
                mAdapter.notifyDataSetChanged();

                scrollToBottom();

                // Empty the message text box.
                textHolder.setText("");
            }
        }

        public void onBackToContacts() {
            startActivity(MainActivity.getIntent(ChatActivity.this));
        }
    }

    public class TextHolder extends BaseObservable {
        private String mText;

        @Bindable
        public String getText() {
            return mText;
        }

        public void setText(String text) {
            mText = text;
            notifyPropertyChanged(BR.text);
        }
    }

    /**
     * Listener for loading the initial messages of a chat room.
     */
    public class OnInitialDataLoaded implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                ChatMessage chatMessage = child.getValue(ChatMessage.class);
                mMessageList.add(chatMessage);
            }
            // Update the UI
            mAdapter.notifyDataSetChanged();

            scrollToBottom();

            mInitialized = true;
            mChatRoomReference.addChildEventListener(new OnMessagesChanged());
            Log.i(TAG, "Chat initialized");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.i(TAG, "Could not initialize chat.");
            // TODO: Inform the user about the error and handle gracefully.

        }
    }

    /**
     * Listener for updating in real time the chat room's messages, after the initial messages have been loaded.
     */
    public class OnMessagesChanged implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
            addChatMessage(chatMessage);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


    // Static helpers

    public static Intent getIntent(Context context, ChatSettings settings) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_SETTINGS, settings);
        return intent;
    }

    // Class implementation

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        // We retrieve the chat settings (username and chat room name)
        mChatSettings = getIntent().getParcelableExtra(KEY_SETTINGS);

        // List configuration
        mMessageList = new ArrayList<>();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mMessageList);
        mBinding.listView.setAdapter(mAdapter);

        // Data binding initialization
        mBinding.setMessage(new TextHolder());
        mBinding.setHandler(new ChatActivityHandler());

        // Firebase initialization
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mChatRoomReference = mFirebaseDatabase.getReference(FIREBASE_KEY_ROOMS).child(mChatSettings.getChatRoom());
        mChatRoomReference.addListenerForSingleValueEvent(new OnInitialDataLoaded());
    }

    /**
     * Adds a chat message to the current list of messages only if it hasn't been previously added.
     *
     * @param chatMessage The chat message to add.
     */
    private void addChatMessage(ChatMessage chatMessage) {
        for (ChatMessage message : mMessageList) {
            if (message.getUuid().equals(chatMessage.getUuid())) return;
        }

        mMessageList.add(chatMessage);
        mAdapter.notifyDataSetChanged();

        scrollToBottom();
    }

    /**
     * Scrolls the list view to the bottom.
     */
    private void scrollToBottom() {
        mBinding.listView.smoothScrollToPosition(mAdapter.getCount() - 1);
    }
}

