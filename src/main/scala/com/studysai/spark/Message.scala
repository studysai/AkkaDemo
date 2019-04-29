package com.studysai.spark

class Message {
}

//worker发送的注册信息
case class RegiestedInfo(id : String, cpu : Int, ram : Int)

//master记录worker发送的注册信息
case class WorkRegiestedInfo(id : String, cpu : Int, ram : Int){
  var time: Long = _
}

//master返回给worker的信息
case class MasterMessage(msg : String)

//worker定时发送心跳
case object SendHeartedBeats

//master定时检测心跳
case class checkHeartBeat()

//worker发送心跳信息
case class HeartBeat(id : String)