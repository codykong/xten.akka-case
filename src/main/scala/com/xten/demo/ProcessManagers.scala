package com.xten.demo

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created with IntelliJ IDEA. 
  * User: cody
  * Date: 2016/11/1 
  * Time: 下午8:47 
  * To change this template use File | Settings | File Templates. 
  */
class ProcessManagers extends Actor{
  override def receive: Receive = {
    println("ProcessManagers");
  }
}
