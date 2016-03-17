package com.mielientiev.bigfiles.storage.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.google.inject.AbstractModule
import com.mielientiev.bigfiles.storage.Routes
import com.mielientiev.bigfiles.storage.controller.DownloadController
import com.mielientiev.bigfiles.storage.service.DownloadService
import com.mielientiev.bigfiles.storage.storage.{S3Client, StorageClient}
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

/**
  * Created by ihor-mielientiev on 3/16/16.
  */
class Bindings extends AbstractModule with ScalaModule {

  private val config = ConfigFactory.load()

  override def configure(): Unit = {
    bindAkkaActorSystem()
    bindRoutes()
    bindServices()
    bindStorageClient()
  }

  private def bindServices(): Unit = {
    bind[DownloadService]
  }

  private def bindStorageClient(): Unit = {
    val accessKey = config.getString("s3.accessKey")
    val secretKey = config.getString("s3.secretKey")
    val bucketName = config.getString("s3.bucketName")
    bind[StorageClient].toInstance(new S3Client(accessKey, secretKey, bucketName))
  }

  private def bindAkkaActorSystem(): Unit = {
    implicit val actorSystem = ActorSystem("demo-actor-system")
    bind[ActorSystem].toInstance(actorSystem)
    bind[ExecutionContext].annotatedWithName("blocking").toInstance(actorSystem.dispatchers.lookup("my-blocking-dispatcher"))
    bind[ExecutionContext].toInstance(actorSystem.dispatcher)
    bind[ActorMaterializer].toInstance(ActorMaterializer())
  }

  private def bindRoutes(): Unit = {
    bind[Routes]
    bind[DownloadController]
  }

}

