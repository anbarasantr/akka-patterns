package com.sksamuel.akka.patterns

import akka.actor.{ActorRef, Actor}
import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Aggregator(types: Seq[Class[_]], target: ActorRef) extends Actor {

  val buffers = types.map(arg => new ListBuffer[AnyRef])

  def receive = {
    case msg: AnyRef =>
      types.indexOf(msg.getClass) match {
        case -1 => unhandled(msg)
        case pos: Int =>
          buffers(pos).append(msg)
          checkForCompleteMessage()
      }
  }

  def checkForCompleteMessage(): Unit = {
    if (buffers.forall(_.size > 0)) {
      val msg = buffers.map(_.remove(0))
      target ! msg
    }
  }
}
