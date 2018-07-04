package com.praktikum.mis.chatapp

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.app.Fragment
import android.net.wifi.p2p.WifiP2pManager
import android.support.v4.view.GravityCompat
import android.view.View;
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.widget.EditText
import android.widget.ListView
import org.json.JSONObject
import java.io.DataOutputStream
import java.util.*


class ChatFragment : Fragment() {

    var activity: MainActivity? = null
    var mManager: WifiP2pManager? = null
    var mView: View? = null
    var listMessages:LinkedList<Message> = LinkedList()

    override fun onDestroy() {
        super.onDestroy()
        listMessages.clear()
        activity?.chatTarget?.clear()
        mView = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                     savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        activity = getActivity() as MainActivity

        mManager = activity?.mManager

        if(activity?.chatTarget != null) {
            for (k in activity?.chatTarget!!) {
                if (k == null) {}
                else if (activity?.socketDictionary?.get(k) == null)
                    connect(k)
            }
        }


        mView = inflater.inflate(R.layout.chat_f, container, false)
        evtListeners()
        var listViewMessages: ListView? = mView?.findViewById(R.id.list_view_messages)
        if(activity?.messages != null && activity?.chatTarget != null) {
            for (i in activity?.messages!!){
                for(j in activity?.chatTarget!!){
                    if(i.fromName.equals(j)) {
                        listMessages.addLast(i)
                    }
                }
            }
        }
        if(activity?.chatTarget != null){
            for(i in activity?.chatTarget!!){
                connect(i)
            }
        }
        var adapter = MsgListAdapter(mView!!.context, listMessages!!)
        listViewMessages?.setAdapter(adapter)
        //listMessages.addLast(Message("asd","asd",true))
        //listMessages.addLast(Message("asd","asd",false))
        adapter.notifyDataSetChanged()
        return mView as View
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun evtListeners(){
        val sendText:EditText? = mView?.findViewById(R.id.inputMsg)
        val btn_send:Button? = mView?.findViewById(R.id.btn_send)
        btn_send?.setOnClickListener {
            if (activity?.chatTarget != null && activity?.chatTarget?.size != 0 ) {
                for (i in activity?.chatTarget!!){
                    if (activity?.socketDictionary?.get(i) != null && !sendText?.text.toString().equals("")) {
                        val sock = activity?.socketDictionary?.get(i)
                        val out = DataOutputStream(sock?.getOutputStream())
                        val test = JSONObject("{\"control\":false,answer:" + activity?.antwort + ",\"name\":\"" + activity?.mReciever?.mAddr + "\"\"message\":\"" + sendText?.text.toString() + "\"}")
                        out.writeUTF(test.toString())
                        out.flush()
                        var m = Message(i, sendText?.text.toString(), true, activity?.antwort!!)
                        activity?.messages?.addLast(m)
                        listMessages.addLast(m)
                        sendText?.setText("")
                    } else {
                        val toast = Toast.makeText(mView?.context, "Connection failure. Could not send message.", Toast.LENGTH_LONG)
                        toast.show()
                    }
                    /*
            else if(!sendText?.text.toString().equals("")) {
                var m = Message("", sendText?.text.toString(), true)
                activity?.messages?.addLast(m)
                listMessages.addLast(m)
                sendText?.setText("")
            }*/
                }
            }
            else{
                val toast = Toast.makeText(mView?.context, "Connection failure. Could not send message.", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    fun update(){
        if(activity?.messages != null && activity?.chatTarget != null) {
            for (i in activity?.messages!!){
                for(j in activity?.chatTarget!!){
                    if(i.fromName.equals(j)) {
                        listMessages.addLast(i)
                    }
                }
            }
        }
    }

    fun connect(k:String) {
        // Picking the first device found on the network.

        val config = WifiP2pConfig()
        config.deviceAddress = k

        mManager?.connect(activity?.mChannel, config, object:WifiP2pManager.ActionListener {

            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
                if(reason == 2){
                    mManager?.connect(activity?.mChannel, config, object:WifiP2pManager.ActionListener {

                        override fun onSuccess() {
                            // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
                        }

                        override fun onFailure(reason: Int) {
                            if(reason == 2){

                            }
                            val toast = Toast.makeText(mView?.context, "Connection failed.", Toast.LENGTH_LONG)
                            toast.show()
                        }
                    })

                }
                val toast = Toast.makeText(mView?.context, "Connection failed.", Toast.LENGTH_LONG)
                toast.show()
            }
        })
    }
}