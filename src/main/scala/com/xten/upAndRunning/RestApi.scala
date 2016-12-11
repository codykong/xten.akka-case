package com.xten.upAndRunning

import akka.actor._
import akka.http.scaladsl
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.pattern.ask
import akka.util.Timeout

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.ExecutionContext

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2016/12/2 
  * Time: 下午6:17 
  */
class RestApi(system: ActorSystem, timeout: Timeout)
  extends RestRoutes {

  implicit val requestTimeout = timeout

  implicit def executionContext = system.dispatcher

  def createBoxOffice = system.actorOf(BoxOffice.props, BoxOffice.name)
}

trait RestRoutes extends BoxOfficeApi
  with EventMarshalling {

  import StatusCodes._

  def routes: Route = eventsRoute ~ eventRoute


  def eventsRoute =
    pathPrefix("events") {
      pathEndOrSingleSlash {
        get {
          // GET /events
          onSuccess(getEvents()) { events =>
            complete(OK)
          }
        }
      }
    }

  def eventRoute =
    pathPrefix("events" / Segment) { event =>
      pathEndOrSingleSlash {
        post {
          entity(as[EventDescription]) { ed=>
            onSuccess(createEvent(event,ed.tickets)){
              case BoxOffice.EventCreated(event) => complete(Created,event)
            }

          }
        }
      }
    }

}

trait BoxOfficeApi {

  import BoxOffice._

  lazy val boxOffice = createBoxOffice()

  def createBoxOffice(): ActorRef

  implicit def executionContext: ExecutionContext

  implicit def requestTimeout: Timeout

  def getEvents() =
    boxOffice.ask(GetEvents).mapTo[Events]

  def createEvent(event:String ,nrOfTickets :Int) =
    boxOffice.ask(CreateEvent(event,nrOfTickets))
      .mapTo[EventResponse]

}
