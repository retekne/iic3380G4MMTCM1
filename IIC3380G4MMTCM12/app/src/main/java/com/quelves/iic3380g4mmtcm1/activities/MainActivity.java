package com.quelves.iic3380g4mmtcm1.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import com.quelves.iic3380g4mmtcm1.Manifest;
import com.quelves.iic3380g4mmtcm1.R;
import com.quelves.iic3380g4mmtcm1.model.ChatSettings;
import com.quelves.iic3380g4mmtcm1.model.User;
import com.quelves.iic3380g4mmtcm1.fragments.ContactsFragment;

public class MainActivity extends AppCompatActivity implements ContactsFragment.OnContactSelected {

    public static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasPhonePermissions()) {
            requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_READ_PHONE_STATE);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            startChat(user);
        }
    }

    private boolean hasPhonePermissions() {
        return ContextCompat.checkSelfPermission(this.getBaseContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED;
    }

    private void startChat(User user) {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getDeviceId()+" ".replace(" ","");
        ChatSettings chatSettings = new ChatSettings(user.mName, mPhoneNumber + "-" + user.mPhoneNumber.replace(" ",""));
        startActivity(ChatActivity.getIntent(MainActivity.this, chatSettings));
    }


}

