package com.studysai.spark

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable
import scala.concurrent.duration._

class Master extends Actor{

  var workers = mutable.Map[String, WorkRegiestedInfo]()

  override def receive : Receive = {
    case "start" => {
      println("server started")
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 12000 millis, self, checkHeartBeat)
    }
    case RegiestedInfo(id, cpu, ram) =>{
      if (!workers.contains(id)) {
        val worker = new WorkRegiestedInfo(id, cpu, ram)
        workers += (id -> worker)
        sender ! MasterMessage(id + "注册成功")
      }
    }
    case HeartBeat(id) => {
      workers(id).time = System.currentTimeMillis()
    }
    case checkHeartBeat =>{
      workers.values.filter(worker => System.currentTimeMillis() - worker.time > 9000).foreach(
        worker => workers.remove(worker.id))
      println("当前活动worker数:" + workers.size)
    }
  }
}

object Master {
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
    val actorSystem = ActorSystem("Master", config)
    val server : ActorRef = actorSystem.actorOf(Props[Master], "Master01")

    server ! "start"
  }
}
