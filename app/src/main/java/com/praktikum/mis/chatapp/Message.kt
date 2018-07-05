package com.praktikum.mis.chatapp


class Message {
    var fromName: String? = null
    var fromAdress: String? = null
    var message: String? = null
    var isSelf: Boolean = false
    var answer: Boolean = false

    constructor() {}

    //fromName enthält die Adresse und fromAdress enthält den Gerätenamen

    constructor(fromName: String, fromAdress :String, message: String, isSelf: Boolean, answer:Boolean) {
        this.fromName = fromName
        this.message = message
        this.isSelf = isSelf
        this.answer = answer
        this.fromAdress = fromAdress
    }
}