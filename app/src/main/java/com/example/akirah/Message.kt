package com.example.akirah

class Message {
    var message:String?=null
    var senderId:String?=null
    var currenttime:String?=null
    constructor(){}

    constructor(message: String?,senderId:String?,currenttime:String?){
        this.message=message
        this.senderId=senderId
        this.currenttime=currenttime
    }
}