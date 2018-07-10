package com.praktikum.mis.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class ChatListAdapter : BaseAdapter {

    var mContext : Context? = null
    var dataSet : LinkedList<OpenChat>? = null
    private var inflater: LayoutInflater?
            = null

    constructor(context: Context, dataSet : LinkedList<OpenChat>, aas:MainActivity){
        this.mContext = context
        this.dataSet = dataSet
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Get view for row item
        val rowView = inflater?.inflate(R.layout.chatlist_item, parent, false)

        // Get GroupName
        val groupNameView = rowView?.findViewById<TextView>(R.id.tx_ChatListGroup)
        val geraete = rowView?.findViewById<TextView>(R.id.tx_ChatListDevice)



        val group = getItem(position) as OpenChat


        groupNameView?.text = group.deviceName
        geraete?.text = group.messages!!.size.toString()

        return rowView!!;
    }

    override fun getItem(position: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemId(position: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}