package com.praktikum.mis.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class chatListAdapter : BaseAdapter {
    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    var mContext : Context? = null
    var dataSet : LinkedList<OpenChat>? = null
    private var inflater: LayoutInflater?
            = null

    constructor(context: Context, dataSet : LinkedList<OpenChat>, aas:MainActivity){
        this.mContext = context
        this.dataSet = dataSet
        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater!!.inflate(R.layout.chatlist_item, parent, false)

        val groupName = rowView.findViewById<TextView>(R.id.tx_ChatListGroup)
        val devName = rowView.findViewById<TextView>(R.id.tx_ChatListDevice)

        var gruppe : String = ""
        var dName = dataSet!![position]!!.deviceName
        if(!dName!!.isEmpty()){
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

        devName.setText(dName)
        groupName.setText(gruppe)

        return rowView
    }

    override fun getItem(position: Int): Any {
        return dataSet!![position]
    }

    override fun getCount(): Int {
        return dataSet!!.size
    }
}