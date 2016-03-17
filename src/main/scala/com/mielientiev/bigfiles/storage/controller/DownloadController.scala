package com.mielientiev.bigfiles.storage.controller

import javax.inject.Inject

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.ContentDispositionTypes._
import akka.http.scaladsl.model.headers.`Content-Disposition`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.mielientiev.bigfiles.storage.service.DownloadService

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
class DownloadController @Inject()(private val downloadService: DownloadService) {
  def apply(): Route = {
    (path("download") & parameter("id")) { id =>
      get {
        complete {
          if (downloadService.isFileExists(id)) streamResponse(id)
          else HttpResponse(404, entity = HttpEntity("File Not Found"))
        }
      }
    }
  }

  private def streamResponse(fileId: String) = {
    HttpResponse(
      entity = HttpEntity.Chunked(
        contentType = ContentTypes.`application/octet-stream`,
        chunks = downloadService.downloadFile(fileId).map(HttpEntity.ChunkStreamPart(_))
      ),
      headers = List(
        `Content-Disposition`(attachment, Map("filename" -> downloadService.getFileName(fileId)))
      )
    )
  }
}
