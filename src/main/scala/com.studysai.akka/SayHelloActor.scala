package com.studysai.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class SayHelloActor extends Actor{
  //1 receive方法会被actor的mailbox调用
  //2 当actor的mailbox接收到消息会调用receive方法
  //3 type Receive = PartialFunction[Any, Unit]
  override def receive : Receive = {
    case "hello" => println("hello 2")
    case "ok"  => println("ok 2")
    case "ping" =>
      println("pong")
      sender ! "pong"
    case "exit"  => println("exit")
      context.stop(self)
      context.system.terminate()//停止actorRef（关闭邮箱）
    case _ => println("none")//退出actorSystem
  }
}

class SenderActor(actorRef : ActorRef) extends Actor {
  override def receive:Receive = {
    case "ping"   => println("ping")
      actorRef ! "ping"
    case "pong" =>
      println("exit")
      context.stop(self)
      context.system.terminate()//停止actorRef（关闭邮箱）
  }
}

object SayHelloActor{
  //1 先创建actorsystem，用于创建actor
  private val actorFactory = ActorSystem("actorFactory")
  //2 创建actor的同时返回actorRef :Props[SayHelloActor]使用反射创建sayHelloActor实例,此事actorRef被actorSystem接管
  private val actorRef : ActorRef = actorFactory.actorOf(Props[SayHelloActor], "sayHelloActor")

  def main(args: Array[String]): Unit = {
    //给 actorRef发消息(发送到mailBox[SayHelloActor]的邮箱)
    /*actorRef ! "hello"
    actorRef ! "exit"*/
    val senderActor = actorFactory.actorOf(Props(new SenderActor(actorRef)), "senderActor")
    senderActor ! "ping"
  }
}