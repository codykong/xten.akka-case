package com.xten.upAndRunning

import akka.actor.{Actor, Props}
import akka.util.Timeout

import scala.concurrent.Future

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2016/12/2 
  * Time: 下午6:34 
  */
object BoxOffice {
  def props(implicit timeout: Timeout) = Props(new BoxOffice)
  def name = "boxOffice"

  case class CreateEvent(name:String,tickets:Int)
  case class GetEvent(name: String)
  case object GetEvents

  case class Events(events: Vector[Event])
  case class Event(name: String, tickets: Int)

  sealed trait EventResponse
  case class EventCreated(event: Event) extends EventResponse
  case object EventExists extends EventResponse
}

class BoxOffice(implicit timeout :Timeout) extends Actor{
  import BoxOffice._
  import context._

  def createTicketSeller(name:String) =
    context.actorOf(TicketSeller.props(name),name)

  def receive = {
    case GetEvents =>
      import akka.pattern.ask
      import akka.pattern.pipe

      def getEvents = context.children.map { child =>
        self.ask(GetEvent(child.path.name)).mapTo[Option[Event]]
      }
      def convertToEvents(f: Future[Iterable[Option[Event]]]) =
        f.map(_.flatten).map(l=> Events(l.toVector))

      pipe(convertToEvents(Future.sequence(getEvents))) to sender()

    case CreateEvent(name,tickets) =>
      def create() = {
        val eventTickets = createTicketSeller(name)
        val newTickets = (1 to tickets).map { ticketId =>
          TicketSeller.Ticket(ticketId)
        }.toVector

        eventTickets ! TicketSeller.Add(newTickets)
        sender() ! EventCreated(Event(name,tickets))
      }
      context.child(name).fold(create())(_ => sender() ! EventExists)

  }
}
