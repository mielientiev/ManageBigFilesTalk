package com.mielientiev.bigfiles.storage.db

import java.util.concurrent.ConcurrentHashMap

import com.mielientiev.bigfiles.storage.model.FileMetadata

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
object MetadataDao {
  private val storage = new ConcurrentHashMap[String, FileMetadata]()

  def saveFileMetadata(fileMetadata: FileMetadata): FileMetadata = {
    storage.putIfAbsent(fileMetadata.fileId, fileMetadata)
    fileMetadata
  }

  def getFileName(fileId: String): Option[String] = Option(storage.get(fileId)).map(_.fileName)

  def getFileMetadata(fileId: String): Option[FileMetadata] = {
    Option(storage.get(fileId))
  }

}



