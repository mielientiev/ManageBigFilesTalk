package com.mielientiev.bigfiles.demo1

import java.util.Base64

import akka.stream.scaladsl.{Flow, Sink, Source}
import com.mielientiev.bigfiles.ImplicitContext

import scala.util.Random


object Demo1 extends ImplicitContext {

  private val words =
    List("Hello", "World", "new", "reactive", "akka", "streams", "scala", "rocks", "java", "dead", "")

  def main(args: Array[String]): Unit = {
    val source = Source(Stream.continually(randomWord()))
    val wordAggregation = Flow[String].filter(_.nonEmpty).grouped(2).map(_.mkString(" "))
    val base64Encoding = Flow[String].map(x => Base64.getEncoder.encodeToString(x.getBytes()))
    val sink = Sink.foreach(println)

    source.via(wordAggregation).via(base64Encoding).runWith(sink)

  }

  private def randomWord() = Random.shuffle(words).head

}
