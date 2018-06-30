package com.praktikum.mis.chatapp

import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import android.net.wifi.WifiManager
import android.net.wifi.p2p.*
import android.os.Looper
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.content_main.*
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.InetSocketAddress
import android.R.attr.fragment
import android.app.Fragment
import android.system.Os.accept
import org.json.JSONObject
import java.io.DataInputStream
//import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import java.io.DataOutputStream
import java.io.DataInputStream.readUTF
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var btnOffOn: Button? = null
    var btnDiscover: Button? = null
    var btnSend: Button? = null
    var listView: ListView? = null
    var read_msg_box: TextView? = null
    var ConnectionStatus: TextView? = null
    var writeMsg: EditText? = null

    var mManager : WifiP2pManager? = null
    var wifimanager: WifiManager? = null
    var mChannel : WifiP2pManager.Channel? = null

    var mReciever : WifiDirectBroadcastReciever? = null
    var mIntentFilter : IntentFilter? = null

    var peers : List<WifiP2pDevice?>? = ArrayList<WifiP2pDevice>()
    var deviceNameArray : Array<String?>? = null
    var deviceArray : Array<WifiP2pDevice?>? = null

    var chatTarget:LinkedList<String> = LinkedList()
    var messages: LinkedList<Message>? = null
    var socketDictionary: HashMap<String,Socket>? = null
    val mServerSocket = ServerSocket(12345)
    var antwort = false
    var chat_fragment:ChatFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        messages = LinkedList()
        socketDictionary = HashMap()
        initialWork()
        extListener()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun extListener(){

        btnOffOn?.setOnClickListener{
            if(wifimanager != null){
                (wifimanager as WifiManager).isWifiEnabled = !((wifimanager as WifiManager).isWifiEnabled)
                if((wifimanager as WifiManager).isWifiEnabled){
                    (btnOffOn as Button).text = "Turn Wifi On"
                }else{
                    (btnOffOn as Button).text = "Turn Wifi Off"
                }
            }
        }

        btnDiscover?.setOnClickListener{
            mManager?.discoverPeers(mChannel, object  : WifiP2pManager.ActionListener{
                override fun onFailure(reason: Int) {
                    connectionStatus?.text="Discovery Starting Failure"
                }

                override fun onSuccess() {
                    connectionStatus?.text="Discovery Started"
                }

            } )
        }

        listView?.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(deviceArray != null) {
                    val device: WifiP2pDevice? = (deviceArray as Array)[position]
                    val config : WifiP2pConfig = WifiP2pConfig()
                    config.deviceAddress=device?.deviceAddress
                    chatTarget.push(config.deviceAddress)
                    mManager?.connect(mChannel,config, object : WifiP2pManager.ActionListener{
                        override fun onFailure(reason: Int) {
                            Toast.makeText(applicationContext, "Connection failed", Toast.LENGTH_SHORT ).show()
                        }

                        override fun onSuccess() {
                            Toast.makeText(applicationContext, "Connected to " + device?.deviceName, Toast.LENGTH_SHORT ).show()
                        }
                    } )
                    val fm = fragmentManager
                    val ft = fm.beginTransaction()
                    chat_fragment = ChatFragment()
                    ft.add(android.R.id.content, chat_fragment, "chatFrag")
                    ft.addToBackStack("chatFrag")
                    ft.commit()
                }
            }

        }
    }

    private fun initialWork() {
         //UI Elemente mit Layout verbinden
        btnOffOn = findViewById(R.id.onOff)
        btnDiscover = findViewById(R.id.discover)
        btnSend = findViewById(R.id.sendButton)

        listView =findViewById(R.id.peerListView)

        read_msg_box = findViewById(R.id.readMsg)

        ConnectionStatus = findViewById(R.id.connectionStatus)

        writeMsg = findViewById(R.id.writeMsg)

        wifimanager =  applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
        mChannel = (mManager as WifiP2pManager).initialize(this, Looper.getMainLooper(), null) as WifiP2pManager.Channel

        mReciever = WifiDirectBroadcastReciever((mManager as WifiP2pManager), (mChannel as WifiP2pManager.Channel), this)

        mIntentFilter = IntentFilter()
        (mIntentFilter as IntentFilter).addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        (mIntentFilter as IntentFilter).addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        (mIntentFilter as IntentFilter).addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        (mIntentFilter as IntentFilter).addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)


    }

    var peerlistListener : WifiP2pManager.PeerListListener = object :WifiP2pManager.PeerListListener{
        override fun onPeersAvailable(peerList: WifiP2pDeviceList?) {
            if((peerList?.deviceList?.equals(peers)) == false){
                (peers as ArrayList).clear()
                (peers as ArrayList).addAll(peerList.deviceList)

                deviceNameArray = arrayOfNulls(peerList.deviceList.size)
                deviceArray = arrayOfNulls(peerList.deviceList.size)

                var index : Int = 0

                peerList.deviceList.forEach{
                    (deviceNameArray as Array<String?>)[index]= it.deviceName as String
                    (deviceArray as Array<WifiP2pDevice>)[index] = it
                    index ++
                }

                var adapter : ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_expandable_list_item_1,deviceNameArray)
                listView?.adapter = adapter

                if((peers as ArrayList).size == 0){
                    Toast.makeText(applicationContext,"No Device Found", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    var connectionInfoListener : WifiP2pManager.ConnectionInfoListener = object : WifiP2pManager.ConnectionInfoListener{
        override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
            val groupOwner : InetAddress? = info?.groupOwnerAddress

            if(info?.groupFormed == true && info?.isGroupOwner == true){
                connectionStatus.text = "Host"

                val mSocket = mServerSocket.accept()
                val mDataInputStream = DataInputStream(mSocket.getInputStream())
                val mDataOutputStream = DataOutputStream(mSocket.getOutputStream())
                val test = JSONObject("{\"control\":true,answer:" + antwort + ",\"name\":\"" + mReciever?.mAddr + "\"}")
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
                            messages?.addLast(Message(devName,message,false, aw))
                            chat_fragment?.update()
                        }
                        else if(control){
                            if(socketDictionary?.get(devName) == null) {
                                socketDictionary?.put(devName, mSocket)
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
                }
            }else if(info?.groupFormed == true){
                connectionStatus.text = "Client"

                val mSocket = Socket()
                mSocket.bind(null)
                mSocket.connect(InetSocketAddress(groupOwner, 12345), 500)
                val mDataInputStream = DataInputStream(mSocket.getInputStream())
                val mDataOutputStream = DataOutputStream(mSocket.getOutputStream())
                val test = JSONObject("{\"control\":true,answer:" + antwort + ",\"name\":\"" + mReciever?.mAddr + "\"}")
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
                            messages?.addLast(Message(devName,message,false, aw))
                            chat_fragment?.update()
                        }
                        else if(control){
                            val devName = json.getString("name")
                            if(socketDictionary?.get(devName) == null) {
                                socketDictionary?.put(devName, mSocket)
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
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        registerReceiver(mReciever, mIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReciever)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //val ft = fragmentManager.beginTransaction()
        //val newFragment = ChatFragment()
        //ft.add(android.R.id.content, newFragment)
        //ft.add(R.layout.chat_fragment, newFragment)
        //val fm = fragmentManager
        //val ft = fm.beginTransaction()
        //val fragment = ChatFragment()
        //ft.add(android.R.id.content, fragment, "chatFrag")
        //ft.addToBackStack("chatFrag")
        //ft.commit()
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
