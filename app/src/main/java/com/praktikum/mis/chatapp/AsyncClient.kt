package com.praktikum.mis.chatapp

import android.os.AsyncTask
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class AsyncClient: AsyncTask<String, Void, String> {
    var activity:MainActivity
    constructor(activity:MainActivity){
        this.activity = activity
    }

    override fun doInBackground(vararg params: String?): String {
        val mSocket = Socket()
        //mSocket.bind(null)
        try {
            mSocket.connect(InetSocketAddress(params[0]!!.substring(1), 12345), 500)
        }catch(e: Exception){
            val text = ""
        }
        val mDataInputStream = DataInputStream(mSocket.getInputStream())
        val mDataOutputStream = DataOutputStream(mSocket.getOutputStream())
        val test = JSONObject("{\"control\":true,answer:" + activity.antwort + ",\"name\":\"" + activity.mReciever?.mAddr + "\"}")
        mDataOutputStream.writeUTF(test.toString())
        mDataOutputStream.flush()
        try {
            while (true) {
                val msg = mDataInputStream.readUTF()
                val json = JSONObject(msg)
                val control = json.getBoolean("control")
                val devName = json.getString("name")
                val aw = json.getBoolean("answer")
                if(!control) {
                    val message = json.getString("message")
                    activity.messages?.addLast(Message(devName,message,false, aw))
                    activity.chat_fragment?.update_necessary = true
                }
                else if(control){
                    val devName = json.getString("name")
                    if(activity.socketDictionary?.get(devName) == null) {
                        activity.socketDictionary?.put(devName, mSocket)
                        activity.inputDictionary?.put(devName, mDataInputStream)
                        activity.outputDictionary?.put(devName, mDataOutputStream)
                    }
                    else{
                        try {
                            mDataOutputStream.close();
                            mDataInputStream.close();
                            mSocket.close();
                        } catch (e: Exception) {
                            System.out.println("Error closing the socket and streams");
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            val test = ""
            // reach end of file
            System.out.print(ex)
        }
        return ""
    }
}