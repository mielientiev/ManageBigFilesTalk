package com.mielientiev.bigfiles

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route

/**
  * Created by ihor-mielientiev on 3/3/16.
  */
trait HttpServer extends ImplicitContext {

  def main(args: Array[String]) {
    Http().bindAndHandle(routes, "0.0.0.0", 9000).onFailure {
      case e =>
        println(s"Server bindings failed with ${e.getMessage}")
        system.shutdown()
    }
  }

  val routes: Route
}
