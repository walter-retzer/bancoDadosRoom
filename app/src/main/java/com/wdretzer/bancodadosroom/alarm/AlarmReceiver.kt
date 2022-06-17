package com.wdretzer.bancodadosroom.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Vibrator
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wdretzer.bancodadosroom.R
import com.wdretzer.bancodadosroom.telas.ListaBD
import com.wdretzer.bancodadosroom.telas.ListTodayActivity


class AlarmReceiver : BroadcastReceiver() {

    var vibrator: Vibrator? = null
    var titulo: String = " "
    var descricao: String = " "
    var idNotification: Int = 0

    override fun onReceive(context: Context, intent: Intent?) {

        idNotification = System.currentTimeMillis().toInt()
        titulo = intent?.getStringExtra("Titulo").toString()
        descricao = intent?.getStringExtra("Description").toString()

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

        // ListaBD iniciará quando o usuario clicar sobre o botão de ação da notificação :
        val snoozeIntent = Intent(context, ListaBD::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val dismissPendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                idNotification,
                snoozeIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        // ListTodayActivity iniciará quando o usuario clicar sobre a Notificação:
        val intent = Intent(context, ListTodayActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                idNotification,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Lembrete: $titulo + $idNotification")
                .setContentText(descricao)
                .setSmallIcon(R.drawable.icon_reminder)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(dismissPendingIntent)
                .addAction(
                    R.drawable.icon_reminder,
                    "Silenciar Lembrete",
                    dismissPendingIntent
                )
                .setAutoCancel(true)


            notify(idNotification, build.build())


        }


        //Habilita vibração do dispositivo quando a Notificação for exibida:
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator?.vibrate(3000)
        Toast.makeText(context, "Alarme Ativo!", Toast.LENGTH_LONG).show()

        //Inicia o toque de alarme do dispositivo:
        val i = Intent(context, AlarmRing::class.java)
        context.startService(i)


    }

    companion object {
        const val NOTIFICATION_ID = 100
        const val REQUEST_CODE = 1000
        const val NOTIFICATION_CHANNEL_ID = "10"
    }
}

