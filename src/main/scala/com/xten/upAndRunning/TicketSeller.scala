package com.xten.upAndRunning

import akka.actor.{Actor, Props}

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2016/12/6 
  * Time: 下午4:24 
  */
object TicketSeller{
  def props(event: String) = Props(new TicketSeller(event))

  case class Add(tickets: Vector[Ticket])
  case class Ticket(id:Int)
  case class Tickets(event: String,
                     entries: Vector[Ticket] = Vector.empty[Ticket])


}

class TicketSeller(event:String) extends Actor{
  import TicketSeller._

  var tickets = Vector.empty[Ticket]

  override def receive: Receive = {
    case Add(newTickets) =>tickets = tickets ++ newTickets
  }
}
