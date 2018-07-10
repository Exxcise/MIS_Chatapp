package com.praktikum.mis.chatapp

import android.app.Fragment
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import java.util.*

class ChatListFragment: Fragment(){

    var activity: MainActivity? = null
    var mManager: WifiP2pManager? = null
    var mView: View? = null
    var listMessages: LinkedList<Message> = LinkedList()
    var adapter:ChatListAdapter?=null;
    var chatList:LinkedList<OpenChat> = LinkedList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        activity = getActivity() as MainActivity

        mManager = activity?.mManager

        mView = inflater.inflate(R.layout.chat_list, container, false)



        var bla= activity!!.messages!!.groupBy{it.fromName}

        bla.forEach{
            chatList.add(OpenChat(LinkedList( it.value)))
        }


        adapter=ChatListAdapter(mView!!.context,chatList,activity!!)

        var listView: ListView? = mView?.findViewById(R.id.list_view_chats)
        listView?.setAdapter(adapter);

        adapter?.notifyDataSetChanged()
        return mView as View
    }


}