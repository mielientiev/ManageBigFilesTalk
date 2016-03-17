package com.mielientiev.bigfiles.demo2

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.mielientiev.bigfiles.{Hasher, HttpServer}

import scala.concurrent.Future

object Demo2 extends HttpServer {

  override val routes: Route = {
    (path("upload") & post) {
      fileUpload("fileData") {
        case (metadata, bodyStream: Source[ByteString, Any]) =>

          val chunkLength: Future[Seq[String]] =
            bodyStream
              .map { data =>
                val hash = sha256.getHash(data.toArray)
                println(s"Length ${data.length}. Chunk SHA-256: $hash")
                hash
              }.runWith(Sink.seq)

          onSuccess(chunkLength)(x => complete(x.mkString("\n")))
      }
    }
  }
  private val sha256 = new Hasher("SHA-256")


}
