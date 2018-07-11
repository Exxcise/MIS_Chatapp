package com.praktikum.mis.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*


class ChatListAdapter : BaseAdapter {
    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    var mContext : Context? = null
    var dataSet : LinkedList<OpenChat>? = null

    var target: String? = null

    private var inflater: LayoutInflater?
            = null

    constructor(context: Context, dataSet : LinkedList<OpenChat>, aas:MainActivity){
        this.mContext = context
        this.dataSet = dataSet
        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // Get view for row item
        val rowView = inflater?.inflate(R.layout.chatlist_item, parent, false)

        // Get GroupName
        val groupNameView = rowView?.findViewById<TextView>(R.id.tx_ChatListGroup)
        val geraete = rowView?.findViewById<TextView>(R.id.tx_ChatListDevice)



        val group = getItem(position) as OpenChat

        target=group.deviceAdress
        groupNameView?.text = group.deviceName
        geraete?.text = group.messages!!.filter { !it.isSelf }.size.toString() + " Nachrichten"

        return rowView!!;

    }

    override fun getItem(position: Int): Any {
        return dataSet!![position]
    }

    override fun getCount(): Int {
        return dataSet!!.size
    }
}