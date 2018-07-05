package com.praktikum.mis.chatapp

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.*
import java.util.List

class MsgListAdapter : BaseAdapter {

	var mcontext:Context?=null
    var messagesItems: LinkedList<Message>
	var activity:MainActivity

	constructor (context:Context, navDrawerItems:LinkedList<Message>, aas:MainActivity) {
		mcontext = context;
		messagesItems = navDrawerItems;
		this.activity = aas
	}

	override fun getCount():Int {
		return messagesItems.size
	}

	override fun getItem(position:Int):Message {
		return messagesItems.get(position);
	}

	override fun getItemId(position:Int):Long {
		return position.toLong();
	}

	override fun getView(position:Int, convertView:View?, parent:ViewGroup?):View {

		/**
		 * The following list not implemented reusable list items as list items
		 * are showing incorrect data Add the solution if you have one
		 * */
		var m = messagesItems.get(position)
        var fromName = m.fromName
		var mInflater:LayoutInflater = mcontext?.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var convertView:View? = null
		// Identifying the message owner
		if (messagesItems.get(position).isSelf) {
			// message belongs to you, so load the right aligned layout
			convertView = mInflater.inflate(R.layout.msg_right,	null)
            fromName = "me";
		} else {
			// message belongs to other person, load the left aligned layout
			convertView = mInflater.inflate(R.layout.msg_left,null)
		}
		var fromAdr = ""
		for(i in activity.deviceArray!!) {
			if (i!!.deviceAddress == m.fromName) {
				fromAdr = i!!.deviceName
			}
		}
		m.fromAdress = fromAdr

		var lblFrom:TextView = convertView?.findViewById(R.id.lblMsgFrom) as TextView
        var txtMsg:TextView = convertView?.findViewById(R.id.txtMsg) as TextView

		if(!fromName.equals("me"))
		fromName = fromAdr
		txtMsg.setText(m.message);
		lblFrom.setText(fromName);

		return convertView;
	}
}