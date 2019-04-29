package com.studysai.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Server extends Actor{

  override def receive : Receive = {
    case "start" => println("server started")
    case ClientMessage(clientMsg) =>
      println(clientMsg)
      clientMsg match {
        case "do you love me" => sender ! ClientMessage("yes I do")
        case _ => sender ! ClientMessage("emmmmm")
      }
  }
}

object Server{
  def main(args: Array[String]): Unit = {
    val host = "127.0.0.1"
    val port = 9999
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
       """.stripMargin)

    //创建actorSystem
    val actorSystem = ActorSystem("ChartServer", config)
    val server : ActorRef = actorSystem.actorOf(Props[Server], "Server")

    server ! "start"
  }
}