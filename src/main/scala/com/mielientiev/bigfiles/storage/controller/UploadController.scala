package com.mielientiev.bigfiles.storage.controller

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject
import com.mielientiev.bigfiles.storage.model.FileMetadata
import com.mielientiev.bigfiles.storage.service.UploadService

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
class UploadController @Inject()(private val uploadService: UploadService) {

  def apply(): Route = {
    (path("upload") & post) {
      fileUpload("fileData") {
        case (metadata, bodyStream) =>
          val uploadFile: Future[FileMetadata] =
            uploadService.uploadFile(metadata.fileName, bodyStream)

          onComplete(uploadFile) {
            case Success(meta) => complete(s"Uploaded. FileId: ${meta.fileId}")
            case Failure(e) => complete(HttpResponse(500))
          }
      }
    }
  }
}
