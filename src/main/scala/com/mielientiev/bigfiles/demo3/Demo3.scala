package com.mielientiev.bigfiles.demo3

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Sink
import com.mielientiev.bigfiles._

import scala.concurrent.Future

object Demo3 extends HttpServer {

  val sha256 = new Hasher("SHA-256")
  val chunkSize: Int = 5 * 1024 * 1024

  override val routes: Route = {
    (path("upload") & post) {
      fileUpload("fileData") {
        case (metadata, bytesSource) =>

          val chunkLength: Future[Seq[String]] =
            bytesSource
              .transform(() => Chunker.chunker(chunkSize))
              .map { data =>
                val hash = sha256.getHash(data.toArray)
                println(s"Length ${data.length}. Chunk SHA-256: $hash")
                hash
              }.runWith(Sink.seq)

          onSuccess(chunkLength)(x => complete(x.mkString("\n")))
      }
    }
  }
}
