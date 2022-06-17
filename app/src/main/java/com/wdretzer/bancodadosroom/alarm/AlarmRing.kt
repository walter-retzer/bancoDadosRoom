package com.wdretzer.bancodadosroom.alarm

import android.app.Service
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder


class AlarmRing : Service() {

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //activating alarm sound
        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        r = RingtoneManager.getRingtone(this, alarmUri)
        //if (r!!.isPlaying) r!!.stop() else r!!.play()

        r!!.stop()
        r!!.play()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        r!!.stop()
    }

    companion object {
        var r: Ringtone? = null
    }
}
