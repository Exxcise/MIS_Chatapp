package com.praktikum.mis.chatapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast

class WifiDirectBroadcastReciever(mManager : WifiP2pManager, mChannel : WifiP2pManager.Channel, mActivity : MainActivity) : BroadcastReceiver() {

    val mManager : WifiP2pManager? = mManager
    val mChannel : WifiP2pManager.Channel? = mChannel
    val mActivity : MainActivity? = mActivity

    override fun onReceive(context: Context?, intent: Intent?) {
        val action : String? = intent?.action

        when (action){
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state : Int = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

                if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                    Toast.makeText(context,"Wifi is ON", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context,"Wifi is OFF", Toast.LENGTH_SHORT).show()
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if(mManager!=null){
                    mManager.requestPeers(mChannel,mActivity?.peerlistListener)
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                if(mManager == null){
                    return;
                }

                val networkInfo : NetworkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                if(networkInfo.isConnected){
                    mManager.requestConnectionInfo(mChannel, mActivity?.connectionInfoListener)
                }else{
                    mActivity?.ConnectionStatus?.text = "Device Disconnected"
                }

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {}
        }
    }
}