package com.praktikum.mis.chatapp

import android.os.AsyncTask
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class AsyncSend: AsyncTask<String, Void, String> {
    var activity:MainActivity
    constructor(activity:MainActivity){
        this.activity = activity
    }

    override fun doInBackground(vararg params: String?): String {
        var test = params[1]
        var out = activity.outputDictionary?.get(params[0])
        out?.writeUTF(test)
        out?.flush()
        return ""
    }
}