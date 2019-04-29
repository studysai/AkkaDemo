package com.studysai.spark

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

class Worker(serverHost : String, serverPort : Int) extends Actor{

  var masterActorRef : ActorSelection = _

  val id = java.util.UUID.randomUUID().toString

  override def preStart(): Unit = {
    println("worker init")
    masterActorRef = context.actorSelection(s"akka.tcp://Master@${serverHost}:${serverPort}/user/Master01")
    println("masterRef:" + masterActorRef )
  }

  override def receive : Receive = {
    case "start" => {
      println("worker started")
      masterActorRef ! RegiestedInfo(id, 16, 256)
    }
    case MasterMessage(msg) => {
      println(msg)
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartedBeats)
    }
    case SendHeartedBeats =>{
      println("send heartBeat")
      masterActorRef ! HeartBeat(id)
    }
  }
}

object Worker {
  def main(args: Array[String]): Unit = {
    val (host, port, serverHost, serverPort) = ("127.0.0.1", 9998, "127.0.0.1", 9999)
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = $host
         |akka.remote.netty.tcp.port = $port
       """.stripMargin)
    val clientSystem = ActorSystem("client", config)
    val worker = clientSystem.actorOf(Props(new Worker(serverHost, serverPort)), "worker01")
    worker ! "start"
  }
}