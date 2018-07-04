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
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.system.Os.accept
import com.praktikum.mis.chatapp.R.styleable.RecyclerView
import org.json.JSONObject
import java.io.DataInputStream
//import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import java.io.DataOutputStream
import java.io.DataInputStream.readUTF
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    var btnOffOn: Button? = null
    var btnDiscover: Button? = null
    var btnSend: Button? = null
    var listView: ListView? = null
    var read_msg_box: TextView? = null
    var writeMsg: EditText? = null

    var adapterGroupList : GroupListAdapter? = null

    var mManager : WifiP2pManager? = null
    var wifimanager: WifiManager? = null
    var mChannel : WifiP2pManager.Channel? = null

    var mReciever : WifiDirectBroadcastReciever? = null
    var mIntentFilter : IntentFilter? = null
    var mActivity: MainActivity?=null

    var peers : List<WifiP2pDevice?>? = ArrayList<WifiP2pDevice>()
    var deviceNameArray : Array<String?>? = null
    var deviceArray : Array<WifiP2pDevice?>? = null

    var chatTarget:LinkedList<String> = LinkedList()
    var messages: LinkedList<Message>? = null
    var socketDictionary: HashMap<String,Socket>? = null
    val mServerSocket = ServerSocket(12345)
    var antwort = false
    var chat_fragment:ChatFragment? = null

    var aktGruppe : String =""

    var ka_index : Int = 0
    var la_index : Int = 0
    var b_index : Int = 0
    var ge_index : Int = 0
    var ff_index : Int = 0

    var kasseArray : Array<WifiP2pDevice?>? = null
    var lagerArray : Array<WifiP2pDevice?>? = null
    var buroArray : Array<WifiP2pDevice?>? = null
    var gemuseArray  : Array<WifiP2pDevice?>? = null
    var friFleischArray : Array<WifiP2pDevice?>? = null

    var groupList : LinkedList<Chatgroup> = LinkedList<Chatgroup>()

    private lateinit var list1View : ListView



    //TODO: Testen aller Funktionen.


    var timerHandler = Handler()

    var timerRunnable: Runnable = object : Runnable {

        override fun run() {

            mManager?.discoverPeers(mChannel, object  : WifiP2pManager.ActionListener{
                override fun onFailure(reason: Int) {
                    Toast.makeText(applicationContext, "Peer Discovery failure", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess() {
                    Toast.makeText(applicationContext, "Peer Discovery started", Toast.LENGTH_SHORT).show()
                }

            } )
            timerHandler.postDelayed(this, 10000)

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_main)
        setSupportActionBar(toolbar)

        mActivity = this;
        AsyncSocket(mActivity!!).execute("")

        messages = LinkedList()
        socketDictionary = HashMap()
        initialWork()
        extListener()
        makeGroups()

        list1View = findViewById<ListView>(R.id.groupListView)

        val adapter = GroupListAdapter(this,groupList)
        adapterGroupList = adapter
        list1View!!.adapter = adapter



        val context = this
        list1View!!.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(deviceArray != null) {
                    val groupName = (groupList[position]).groupName
                    aktGruppe = groupName

                    if(groupName.equals("Kasse"))
                    {
                        for (i : Int in 0 .. kasseArray!!.size -1){
                            if((kasseArray as Array<WifiP2pDevice>).get(i) != null)
                                chatTarget.push((kasseArray as Array<WifiP2pDevice>).get(i).deviceAddress)
                        }
                    }

                    if(groupName.equals("Lager"))
                    {
                        for (i : Int in 0 .. lagerArray!!.size -1){
                            if((lagerArray as Array<WifiP2pDevice>).get(i) != null)
                                chatTarget.push((lagerArray as Array<WifiP2pDevice>).get(i).deviceAddress)
                        }
                    }

                    if(groupName.equals("Buero"))
                    {
                        for (i : Int in 0 .. buroArray!!.size -1){
                            if((buroArray as Array<WifiP2pDevice>).get(i) != null)
                                chatTarget.push((buroArray as Array<WifiP2pDevice>).get(i).deviceAddress)
                        }
                    }

                    if(groupName.equals("Gemuese"))
                    {
                        for (i : Int in 0 .. gemuseArray!!.size -1){
                            if((gemuseArray as Array<WifiP2pDevice>).get(i) != null)
                                chatTarget.push((gemuseArray as Array<WifiP2pDevice>).get(i).deviceAddress)
                        }
                    }

                    if(groupName.equals("Frisch Fleisch"))
                    {
                        for (i : Int in 0 .. friFleischArray!!.size -1){
                            if((friFleischArray as Array<WifiP2pDevice>).get(i) != null)
                                chatTarget.push((friFleischArray as Array<WifiP2pDevice>).get(i).deviceAddress)
                        }
                    }

                    //val config: WifiP2pConfig = WifiP2pConfig()

                    //config.deviceAddress = device?.deviceAddress
/*
                    if(!chatTarget.isEmpty()) {
                        //mManager?.connect(mChannel, config, object : WifiP2pManager.ActionListener {
                         //   override fun onFailure(reason: Int) {
                         //       Toast.makeText(applicationContext, "Connection failed", Toast.LENGTH_SHORT).show()
                         //   }

                          //  override fun onSuccess() {
                          //      Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
                          //  }
                       // })
                    }
  */              }
                    val fm = fragmentManager
                    val ft = fm.beginTransaction()
                    chat_fragment = ChatFragment()
                    ft.add(android.R.id.content, chat_fragment, "chatFrag")
                    ft.addToBackStack("chatFrag")
                    ft.commit()

            }
        }

        timerHandler.postDelayed(timerRunnable, 0);

       //val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
       // drawer_layout.addDrawerListener(toggle)
       // toggle.syncState()

       // nav_view.setNavigationItemSelectedListener(this)
    }

    private fun makeGroups(){
        var group1 : Chatgroup = Chatgroup("Kasse")
        var group2 : Chatgroup = Chatgroup("Lager")
        var group3 : Chatgroup = Chatgroup("Buero")
        var group4 : Chatgroup = Chatgroup("Gemuese")
        var group5 : Chatgroup = Chatgroup("Frisch Fleisch")

        group1.geraeteOnline = ka_index
        group2.geraeteOnline = b_index
        group3.geraeteOnline = la_index
        group4.geraeteOnline = ge_index
        group5.geraeteOnline = ff_index

        groupList.add(group1)
        groupList.add(group2)
        groupList.add(group3)
        groupList.add(group4)
        groupList.add(group5)

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
                }

                override fun onSuccess() {
                }

            } )
        }

        listView?.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                antwort = false;
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

                kasseArray = arrayOfNulls(peerList.deviceList.size)
                lagerArray = arrayOfNulls(peerList.deviceList.size)
                buroArray = arrayOfNulls(peerList.deviceList.size)
                gemuseArray  = arrayOfNulls(peerList.deviceList.size)
                friFleischArray = arrayOfNulls(peerList.deviceList.size)

                //Gruppen[Tag]:
                //Kasse[KA]
                //Lager[LA]
                //Buro[B]
                //Gemuse[GE]
                //Frischfleisch[FF]
                //Arrays erstellt und die Devices werden anhand der obigen Tags im Device Namen in das jeweilige Array eingeordnet



                var index : Int = 0
                ka_index  = 0
                la_index  = 0
                b_index  = 0
                ge_index  = 0
                ff_index = 0

                peerList.deviceList.forEach{
                    (deviceNameArray as Array<String?>)[index]= it.deviceName as String
                    (deviceArray as Array<WifiP2pDevice>)[index] = it

                    if(it.deviceName.contains("KA")) {
                        (kasseArray as Array<WifiP2pDevice>)[ka_index] = it
                        ka_index++
                    }
                    if(it.deviceName.contains("LA")) {
                        (lagerArray as Array<WifiP2pDevice>)[la_index] = it
                        la_index++
                    }
                    if(it.deviceName.contains("B")) {
                        (buroArray as Array<WifiP2pDevice>)[b_index] = it
                        b_index++
                    }
                    if(it.deviceName.contains("GE")) {
                        (gemuseArray as Array<WifiP2pDevice>)[ge_index] = it
                        ge_index++
                    }
                    if(it.deviceName.contains("FF")) {
                        (friFleischArray as Array<WifiP2pDevice>)[ff_index] = it
                        ff_index++
                    }

                    index ++
                }

                var adapter : ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_expandable_list_item_1,deviceNameArray)
                listView?.adapter = adapter

                groupList.get(0).geraeteOnline = ka_index
                groupList.get(1).geraeteOnline = b_index
                groupList.get(2).geraeteOnline = la_index
                groupList.get(3).geraeteOnline = ge_index
                groupList.get(4).geraeteOnline = ff_index

                adapterGroupList?.notifyDataSetChanged()

                if((peers as ArrayList).size == 0){
                    Toast.makeText(applicationContext,"No Device Found", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    var connectionInfoListener : WifiP2pManager.ConnectionInfoListener = object : WifiP2pManager.ConnectionInfoListener{
        override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
            val groupOwner : InetAddress? = info?.groupOwnerAddress

            if(info?.groupFormed == true && info?.isGroupOwner == false){

            }else if(info?.groupFormed == true){

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
        timerHandler.postDelayed(timerRunnable, 0);
    }

    override fun onPause() {
        super.onPause()
        timerHandler.removeCallbacks(timerRunnable);
        unregisterReceiver(mReciever)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
