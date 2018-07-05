package com.praktikum.mis.chatapp

import java.util.*

class OpenChat{

    var deviceAdress : String? = null
    var deviceName : String? = null
    var messages : LinkedList<Message>? = null

    constructor(messages : LinkedList<Message>){
        this.deviceAdress = messages.first.fromName
        this.deviceName = messages.first.fromAdress
        this.messages = messages
    }
}