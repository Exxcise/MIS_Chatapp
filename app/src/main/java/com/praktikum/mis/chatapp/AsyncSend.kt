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
        if(activity?.socketDictionary?.get(params[0])!!.isConnected() && !activity?.socketDictionary?.get(params[0])!!.isClosed() && !activity?.socketDictionary?.get(params[0])!!.isOutputShutdown()){
            out?.writeUTF(test)
            out?.flush()
        }
        return ""
    }
}