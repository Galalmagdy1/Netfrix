package com.example.netfrix

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("trace", "onReceive: ${intent.action}")
        //Incoming call or declining the call
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                Log.d("trace", "Incoming call")
                Toast.makeText(context, "Incoming call!", Toast.LENGTH_LONG).show()
            }

        }
    }

}