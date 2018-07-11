package com.praktikum.mis.chatapp

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.app.Fragment
import android.net.wifi.p2p.WifiP2pManager
import android.support.v4.view.GravityCompat
import android.view.View;
import kotlinx.android.synthetic.main.activity_main.*
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.os.AsyncTask
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.widget.*
import org.json.JSONObject
import java.io.DataOutputStream
import java.util.*


class ChatFragment : Fragment() {

    var activity: MainActivity? = null
    var mManager: WifiP2pManager? = null
    var mView: View? = null
    var listMessages:LinkedList<Message> = LinkedList()
    var adapter:MsgListAdapter? =null
    var update_necessary:Boolean = false

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

        var timerHandler = Handler()
        var timerRunnable: Runnable = object : Runnable {

            override fun run() {
                if(update_necessary)
                    update()
                timerHandler.postDelayed(this, 1000)

            }
        }
        timerHandler.postDelayed(timerRunnable, 0);


        mView = inflater.inflate(R.layout.chat_f, container, false)

        var toolb: TextView? = mView?.findViewById(R.id.toolbar_title)
        toolb?.setText(activity?.toolText)
        evtListeners()
        var listViewMessages: ListView? = mView?.findViewById(R.id.list_view_messages)
        if(activity?.messages != null && activity?.chatTarget != null) {
            for (i in activity?.messages!!){
                for(j in activity?.chatTarget!!){
                    if(i.fromName.equals(j) && ((i.answer == activity?.isAnswer && !i.isSelf) || (!i.answer == activity?.isAnswer && i.isSelf))) {
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
        adapter = MsgListAdapter(mView!!.context, listMessages!!, activity!!)
        listViewMessages?.setAdapter(adapter)
        //listMessages.addLast(Message("asd","asd",true))
        //listMessages.addLast(Message("asd","asd",false))
        adapter?.notifyDataSetChanged()
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
                    if (activity?.outputDictionary?.get(i) != null && !sendText?.text.toString().equals("")) {
                        //var sock = activity?.socketDictionary?.get(i)
                        //var out = DataOutputStream(sock!!.getOutputStream())
                        var out = activity?.outputDictionary?.get(i)
                        var test = JSONObject("{\"control\":false,\"answer\":" + activity?.antwort + ",\"name\":\"" + activity?.mReciever?.mAddr + "\",\"message\":\"" + sendText?.text.toString() + "\"}")

                        AsyncSend(activity!!).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,i,test.toString())
                        //out?.writeUTF(test.toString())
                        //out?.flush()
                        //var m = Message(i, sendText?.text.toString(), true, activity?.antwort!!)
                        //out?.writeUTF(test.toString())
                        //out?.flush()

                        var fromName : String = ""

                        if(activity != null && activity?.deviceArray != null) {
                            for (d: WifiP2pDevice? in activity?.deviceArray as Array) {
                                if(d != null && d.deviceAddress.equals(i)){
                                    fromName = d.deviceName
                                }
                            }
                        }

                        var m = Message(i, fromName , sendText?.text.toString(), true, activity?.antwort!!)

                        activity?.messages?.addLast(m)
                        listMessages.addLast(m)
                        adapter?.notifyDataSetChanged()
                        sendText?.setText("")
                    } else {
                        connect(i)
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
                val toast = Toast.makeText(mView?.context, "No devices found.", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    fun update(){
        if(activity?.messages != null && activity?.chatTarget != null) {
            for (i in activity?.messages!!){
                for(j in activity?.chatTarget!!){
                    if(i.fromName.equals(j) && !listMessages.contains(i) && ((i.answer == activity?.isAnswer && !i.isSelf) || (!i.answer == activity?.isAnswer && i.isSelf))) {
                        listMessages.addLast(i)
                    }
                }
            }
        }
        adapter?.notifyDataSetChanged()
        update_necessary = false
    }

    fun connect(k:String) {
        // Picking the first device found on the network.

        val config = WifiP2pConfig()
        config.deviceAddress = k
        config.wps.setup = WpsInfo.PBC
        config.groupOwnerIntent = 0

        activity?.mManager?.connect(activity?.mChannel, config, object:WifiP2pManager.ActionListener {

            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
                if(reason == 2){
                    connect(k)
                }
                try {
                    val toast = Toast.makeText(mView?.context, "Connection failed.", Toast.LENGTH_LONG)
                    toast.show()
                }catch(ex:Exception){

                }
            }
        })
    }
}