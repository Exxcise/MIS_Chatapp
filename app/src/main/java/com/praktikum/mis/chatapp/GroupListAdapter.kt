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

class GroupListAdapter: BaseAdapter{

    private var context : Context? = null
    private var dataSource: LinkedList<Chatgroup> = LinkedList()
    private var inflater: LayoutInflater?
            = null


    override fun getCount(): Int {
        return dataSource.size
    }

    constructor (context:Context, dataSource:LinkedList<Chatgroup>) {
        this.context = context;
        this.dataSource = dataSource;
        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        // Get view for row item
        val rowView = inflater?.inflate(R.layout.group_item, parent, false)

        // Get GroupName
        val groupNameView = rowView?.findViewById<TextView>(R.id.groupItem_Name)
        val geraete = rowView?.findViewById<TextView>(R.id.groupItem_Geraeteanz)



        val group = getItem(position) as Chatgroup


        groupNameView?.text = group.groupName
        geraete?.text = group.geraeteOnline.toString()

        return rowView
    }
}

