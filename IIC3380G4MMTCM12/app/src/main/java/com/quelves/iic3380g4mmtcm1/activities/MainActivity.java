package com.quelves.iic3380g4mmtcm1.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.quelves.iic3380g4mmtcm1.R;
import com.quelves.iic3380g4mmtcm1.model.ChatSettings;
import com.quelves.iic3380g4mmtcm1.model.User;
import com.quelves.iic3380g4mmtcm1.fragments.ContactsFragment;

public class MainActivity extends AppCompatActivity implements
        ContactsFragment.OnContactSelected {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // If fragment is already added, replace it.
        if (getSupportFragmentManager().findFragmentByTag(ContactsFragment.TAG) != null) {
            transaction = transaction.replace(R.id.main_container,
                    new ContactsFragment(), null);
        } else {
            transaction = transaction.add(R.id.main_container,
                    new ContactsFragment(), ContactsFragment.TAG);
        }
        transaction.commit();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    /**
     * Called when contact is selected on ContactsFragment.
     *
     * @param user Contact selected.
     */
    @Override
    public void onContactSelected(User user) {
        ChatSettings chatSettings = new ChatSettings(user.mName, user.mPhoneNumber);
        startActivity(ChatActivity.getIntent(MainActivity.this, chatSettings));
    }
}

