package com.wdretzer.bancodadosroom.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.os.Handler
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.telas.ListTodayActivity


class AlarmReceiver : BroadcastReceiver() {

    var ringtone: Ringtone? = null
    var vibrator: Vibrator? = null

    companion object {
        const val NOTIFICATION_ID = 100
        const val EXTRA_NOTIFICATION_ID = "1000"
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val ACTION_CHECK_TASK = "Action Check Task"
    }

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        notifyNotification(context)
    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "App",
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }


    private fun notifyNotification(context: Context) {

        val snoozeIntent = Intent(context, ListTodayActivity::class.java).apply {
            action = ACTION_CHECK_TASK
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }

        val checkAlarmTaskPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                1000,
                snoozeIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, ListTodayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Alarme")
                .setContentText("Alarme Ativo")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
//                .addAction(
//                    R.drawable.icon_reminder,
//                    "Lembrete",
//                    checkAlarmTaskPendingIntent
//                )
                .setAutoCancel(true)

            notify(NOTIFICATION_ID, build.build())

        }

        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(2000)
        Toast.makeText(context, "Alarme Ativo!", Toast.LENGTH_LONG).show()

        var alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone?.play()

        Handler().postDelayed({
            ringtone?.stop()
            vibrator?.cancel()
        }, 60000)
    }

    fun stopRingTone(context: Context) {

        if (ringtone!= null){
            ringtone!!.stop()

            Toast.makeText(context, "Parou o som!", Toast.LENGTH_SHORT).show()
        }

        vibrator?.cancel()
    }

}
