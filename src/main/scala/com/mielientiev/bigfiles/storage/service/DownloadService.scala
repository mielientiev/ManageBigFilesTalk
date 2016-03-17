package com.mielientiev.bigfiles.storage.service

import javax.inject.{Inject, Named}

import akka.stream.ActorAttributes._
import akka.stream.Supervision
import akka.stream.scaladsl.{Source, StreamConverters}
import akka.util.ByteString
import com.mielientiev.bigfiles.storage.db.MetadataDao
import com.mielientiev.bigfiles.storage.model.FileMetadata
import com.mielientiev.bigfiles.storage.storage.StorageClient

import scala.concurrent.ExecutionContext


class DownloadService @Inject()(private val storageClient: StorageClient,
                                @Named("blocking") private implicit val executor: ExecutionContext) {

  private val supervisionStorageStrategy: Supervision.Decider = {
    case e: Throwable =>
      println("Something Went Wrong.... " + e)
      Supervision.Stop
  }

  def getFileName(fileId: String): String = MetadataDao.getFileName(fileId).getOrElse("")

  def isFileExists(fileId: String): Boolean = MetadataDao.getFileMetadata(fileId).exists(_.chunksNum > 0)

  def downloadFile(fileId: String): Source[ByteString, Unit] = {
    val metadata: Option[FileMetadata] = MetadataDao.getFileMetadata(fileId)
    val chunksId: List[String] = metadata.map(formatChunksId).getOrElse(List())
    Source(chunksId)
      .flatMapConcat(objId => getObjectStreamFromStorage(objId))
  }

  private def formatChunksId(metadata: FileMetadata): List[String] = {
    (1 to metadata.chunksNum).map(index => s"${metadata.fileId}_$index").toList
  }

  private def getObjectStreamFromStorage(objectId: String): Source[ByteString, Any] = {
    Source.fromFuture(storageClient.get(objectId)(executor))
      .withAttributes(supervisionStrategy(supervisionStorageStrategy))
      .flatMapConcat(in => StreamConverters.fromInputStream(() => in))
  }

}
