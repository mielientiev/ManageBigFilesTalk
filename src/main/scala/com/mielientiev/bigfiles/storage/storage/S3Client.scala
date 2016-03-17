package com.mielientiev.bigfiles.storage.storage

import java.io.{ByteArrayInputStream, InputStream}

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.event.ProgressEventType._
import com.amazonaws.event.{ProgressEvent, ProgressListener}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.model.UploadResult
import com.amazonaws.services.s3.transfer.{TransferManager, Upload}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
class S3Client(accessKey: String, secretKey: String, bucketName: String) extends StorageClient {

  private val s3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
  private val txManager = new TransferManager(s3Client)

  override def get(chunkId: String)(implicit context: ExecutionContext): Future[InputStream] = {
    Future(s3Client.getObject(bucketName, chunkId).getObjectContent())(context)
  }

  override def put(chunkId: String, chunk: Array[Byte])(implicit context: ExecutionContext): Future[UploadResult] = {
    val promise = Promise[UploadResult]()
    val metadata = new ObjectMetadata()
    metadata.setContentLength(chunk.length.toLong)
    val upload: Upload = txManager.upload(bucketName, chunkId, new ByteArrayInputStream(chunk), metadata)

    upload.addProgressListener(new ProgressListener {
      override def progressChanged(progressEvent: ProgressEvent): Unit = {
        if (progressEvent.getEventType == TRANSFER_FAILED_EVENT ||
          progressEvent.getEventType == TRANSFER_CANCELED_EVENT ||
          progressEvent.getEventType == TRANSFER_COMPLETED_EVENT) {

          promise.complete(Try(upload.waitForUploadResult()))
        }
      }
    })
    promise.future.recoverWith {
      case e: Exception =>
        println("ERROR: " + e.toString)
        Future.failed(e)
    }
  }

}
