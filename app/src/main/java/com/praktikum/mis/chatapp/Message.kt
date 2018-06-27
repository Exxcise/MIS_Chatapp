package com.praktikum.mis.chatapp


class Message {
    var fromName: String? = null
    var message: String? = null
    var isSelf: Boolean = false

    constructor() {}

    constructor(fromName: String, message: String, isSelf: Boolean) {
        this.fromName = fromName
        this.message = message
        this.isSelf = isSelf
    }

}