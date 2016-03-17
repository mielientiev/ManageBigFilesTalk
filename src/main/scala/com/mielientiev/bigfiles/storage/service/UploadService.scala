package com.mielientiev.bigfiles.storage.service

import java.util.UUID
import javax.inject.Named

import akka.stream.ActorAttributes._
import akka.stream.{ActorMaterializer, Supervision}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.google.inject.Inject
import com.mielientiev.bigfiles.Chunker
import com.mielientiev.bigfiles.storage.db.MetadataDao
import com.mielientiev.bigfiles.storage.model.FileMetadata
import com.mielientiev.bigfiles.storage.storage.StorageClient

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
class UploadService @Inject()(implicit val mat: ActorMaterializer,
                              private val storage: StorageClient,
                              @Named("blocking") private implicit val executor: ExecutionContext) {

  def uploadFile(fileName: String, fileStream: Source[ByteString, Any]): Future[FileMetadata] = {
    val fileId = generateFileId()

    val result: Future[Int] = fileStream
      .transform(() => Chunker.chunker(5 * 1024 * 1024))
      .zip(generateChunkIds(fileId))
      .mapAsyncUnordered(1) {
        case (rawChunk, chunkId) =>
          println(s"Uploading $chunkId with size: ${rawChunk.length}")
          storage.put(chunkId, rawChunk.toArray)
      }.runFold(0)((acc, _) => acc + 1)

    result.map(chunkNum => MetadataDao.saveFileMetadata(FileMetadata(fileId, fileName, chunkNum)))
  }

  private def generateFileId(): String = UUID.randomUUID().toString

  private def generateChunkIds(fileId: String): Source[String, Unit] = {
    Source(Stream.from(1).map(index => s"${fileId}_$index"))
  }

}
