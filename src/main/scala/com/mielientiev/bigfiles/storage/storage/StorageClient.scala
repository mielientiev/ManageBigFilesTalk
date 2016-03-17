package com.mielientiev.bigfiles.storage.storage

import java.io.InputStream

import akka.util.ByteString
import com.amazonaws.services.s3.transfer.model.UploadResult

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by ihor-mielientiev on 3/16/16.
  */
trait StorageClient {

  def put(objectId: String, chunk: Array[Byte])(implicit context: ExecutionContext): Future[UploadResult]

  def get(objectId: String)(implicit context: ExecutionContext): Future[InputStream]

}
