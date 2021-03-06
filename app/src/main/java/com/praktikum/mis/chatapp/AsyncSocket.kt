package com.praktikum.mis.chatapp

import android.net.wifi.p2p.WifiP2pDevice
import android.os.AsyncTask
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream

class AsyncSocket: AsyncTask<String, Void, String> {
    var activity:MainActivity
    constructor(activity:MainActivity){
        this.activity = activity
    }

    override fun doInBackground(vararg params: String?): String {
        val mSocket = activity.mServerSocket.accept()
        val mDataInputStream = DataInputStream(mSocket.getInputStream())
        val mDataOutputStream = DataOutputStream(mSocket.getOutputStream())
        val test = JSONObject("{\"control\":true,answer:" + activity.antwort + ",\"name\":\"" + activity.mReciever?.mAddr + "\"}")

        try {
            mDataOutputStream.writeUTF(test.toString())
            mDataOutputStream.flush()
            while (true) {
                val msg = mDataInputStream.readUTF()
                val json = JSONObject(msg)
                val control = json.getBoolean("control")
                val devName = json.getString("name")
                val aw = json.getBoolean("answer")
                if(!control) {
                    val message = json.getString("message")

                    ///NOTIFICATIONS
                    var dName : String = ""
                    if(activity != null && activity?.deviceArray != null) {
                        for (d: WifiP2pDevice? in activity?.deviceArray as Array) {
                            if(d != null && d.deviceAddress.equals(devName)){
                                dName = d.deviceName
                            }
                        }
                    }

                    var gruppe : String = ""
                    if(!dName.isEmpty()){
                        if(dName.contains("KA"))
                            gruppe = "Kasse"
                        if(dName.contains("LA"))
                            gruppe = "Lager"
                        if(dName.contains("B"))
                            gruppe = "Buro"
                        if(dName.contains("GE"))
                            gruppe = "Gemuse"
                        if(dName.contains("FF"))
                            gruppe = "FrischFleisch"
                    }

                    val notService : NotificationService = NotificationService(dName,message,activity.applicationContext)
                    notService.buildNotification()
                    ////
                    activity.messages?.addLast(Message(devName,dName,message,false, aw))

                    activity.chat_fragment?.update_necessary = true
                }
                else if(control){
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
            // reach end of file
            val text = ""
        }
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return ""
    }
}