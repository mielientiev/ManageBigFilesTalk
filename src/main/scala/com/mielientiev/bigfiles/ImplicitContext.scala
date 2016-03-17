package com.mielientiev.bigfiles

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

/**
  * Created by ihor-mielientiev on 3/3/16.
  */
trait ImplicitContext {

  implicit val system = ActorSystem("test_talk")
  implicit val actorMaterializer = ActorMaterializer()
  implicit val exContext = system.dispatcher
}
