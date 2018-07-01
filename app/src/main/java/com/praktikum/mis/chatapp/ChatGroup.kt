package com.praktikum.mis.chatapp

class Chatgroup{
    var groupName : String = ""
    var unreadMessage : Int = 0
    var geraeteOnline : Int = 0

    fun setGrouName(s: String){
        this.groupName =  s
    }

    fun setUnreadMessages(i : Int){
        this.unreadMessage = i
    }

    constructor(groupName: String) {
        this.groupName = groupName

    }
}