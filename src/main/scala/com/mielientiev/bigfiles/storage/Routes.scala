package com.mielientiev.bigfiles.storage

import com.google.inject.Inject
import com.mielientiev.bigfiles.storage.controller.{DownloadController, UploadController}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
class Routes @Inject()(private val downloadController: DownloadController,
                       private val uploadController: UploadController) {

  def apply(): Route = {
    index() ~
      downloadController() ~
      uploadController()
  }

  private def index(): Route = {
    path("") {
      getFromResource("web/index.html")
    }
  }
}
