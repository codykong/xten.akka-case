package com.xten.demo

import akka.actor.{ActorRef, ActorSystem, Props}


/**
  * Created with IntelliJ IDEA. 
  * User: cody
  * Date: 2016/10/31 
  * Time: 下午8:57 
  * To change this template use File | Settings | File Templates. 
  */
object AkkaDemo {

  def main(args: Array[String]) {
    var system = ActorSystem.create("master");
    val processManagersRef :ActorRef =
      system.actorOf(Props[ProcessManagers],"processManagers")
    println("success")
//    processManagersRef ! BrokerForLoan(banks)
  }

}
