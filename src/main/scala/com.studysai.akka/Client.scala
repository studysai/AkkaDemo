package com.studysai.akka

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

class Client(serverHost : String, serverPort : Int) extends Actor{

  var serverActorRef : ActorSelection = _

  override def preStart(): Unit = {
    println("Client init")
    serverActorRef = context.actorSelection(s"akka.tcp://ChartServer@${serverHost}:${serverPort}/user/Server")
    println("serverRef:" + serverActorRef )
  }

  override def receive : Receive = {
    case "start"   => println("client started")
    case msg : String => serverActorRef ! ClientMessage(msg)
    case clientMsg : ClientMessage => println(clientMsg.msg)
  }
}

object Client {
  def main(args: Array[String]): Unit = {
    val (host, port, serverHost, serverPort) = ("127.0.0.1", 9998, "127.0.0.1", 9999)
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
       """.stripMargin)
    val clientSystem = ActorSystem("client", config)
    val client = clientSystem.actorOf(Props(new Client(serverHost, serverPort)), "Client")
    client ! "start"

    var flag = true

    while(flag) {
      val str = StdIn.readLine()
      if ("exit".equals(str))
        flag = false
      client ! str
    }
  }
}

case class ClientMessage(msg : String)