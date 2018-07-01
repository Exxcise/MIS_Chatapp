package com.praktikum.mis.chatapp
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.*
import java.util.List

class GroupListAdapter(private val context: Context,
                       private val dataSource: LinkedList<Chatgroup>) : BaseAdapter(){

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.group_item, parent, false)

        // Get GroupName
        val groupNameView = rowView.findViewById<TextView>(R.id.groupItem_Name)
        val geraete = rowView.findViewById<TextView>(R.id.groupItem_Geraeteanz)

        val group = getItem(position) as Chatgroup


        groupNameView.text = group.groupName
        geraete.text = group.geraeteOnline.toString()

        return rowView
    }
}

