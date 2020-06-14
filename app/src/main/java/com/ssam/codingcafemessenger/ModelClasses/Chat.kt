package com.ssam.codingcafemessenger.ModelClasses

class Chat(var sender: String, var message: String, var receiver: String, var isseen : Boolean,
           var url:String, var messageId: String){
    constructor(): this("", "", "", false, "", "")
}