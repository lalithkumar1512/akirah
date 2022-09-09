package com.example.akirah

class User {
    var name:String?=null
    var email:String?=null
    var uid:String?=null
    var typingto:String?=null
    var state:String?=null
    var time:String?=null
    var date:String?=null


    constructor(){

    }

    constructor(name:String?,email:String?,uid:String?,typingto:String?,state:String?,time:String?,date:String?){
        this.name=name
        this.email=email
        this.uid=uid
        this.typingto=typingto
        this.state=state
        this.time=time
        this.date=date
    }
}