package com.praktikum.mis.chatapp

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.support.v4.app.NotificationBuilderWithBuilderAccessor

class NotificationService {

    var gruppe : String = ""
    var msg : String = ""
    var context : Context? = null

    constructor(g : String, m : String, con : Context){
        this.gruppe = g
        this.msg = m
        this.context = con
    }

    fun buildNotification(){

        var intent : Intent? = Intent(this.context, MainActivity::class.java)
        var vibr : LongArray = LongArray(5)
        var pIntent : PendingIntent = PendingIntent.getActivity(context,System.currentTimeMillis().toInt(), intent, 0 )

        vibr[0] = 1000
        vibr[1] = 1000
        vibr[2] = 1000
        vibr[3] = 1000
        vibr[4] = 1000


        val notify : Notification = Notification.Builder(this.context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle("Neue Nachricht : " + gruppe)
                .setContentText(msg)
                .setContentIntent(pIntent)
                .setVibrate(vibr)
                .build()

        val notifyM : NotificationManager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifyM.notify(0, notify)

    }
}