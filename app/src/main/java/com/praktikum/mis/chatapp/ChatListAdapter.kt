package com.praktikum.mis.chatapp

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

class chatListAdapter : BaseAdapter {

    var mContext : Context? = null
    var dataSet : LinkedList<OpenChat>? = null

    constructor(context: Context, dataSet : LinkedList<OpenChat>){
        this.mContext = context
        this.dataSet = dataSet
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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