package com.mielientiev.bigfiles.storage

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.google.inject.Guice
import com.mielientiev.bigfiles.storage.config.Bindings

import scala.concurrent.ExecutionContext


object StorageServer {

  def main(args: Array[String]) {
    val injector = Guice.createInjector(new Bindings)
    implicit val system = injector.getInstance(classOf[ActorSystem])
    implicit val context = injector.getInstance(classOf[ExecutionContext])
    implicit val materializer = injector.getInstance(classOf[ActorMaterializer])

    val routes: Routes = injector.getInstance(classOf[Routes])

    Http().bindAndHandle(routes(), "0.0.0.0", 9000).onFailure {
      case e => system.shutdown()
    }
    println("Server started. http://localhost:9000/")
  }

}


