package com.mielientiev.bigfiles

import akka.stream.stage.{Context, PushPullStage, SyncDirective, TerminationDirective}
import akka.util.ByteString


object Chunker {

  def chunker(chunkSize: Int) = new PushPullStage[ByteString, ByteString] {

    private var buffer = ByteString.empty

    override def onPush(elem: ByteString, ctx: Context[ByteString]): SyncDirective = {
      buffer ++= elem
      emitChunkOrPull(ctx)
    }

    override def onPull(ctx: Context[ByteString]): SyncDirective = emitChunkOrPull(ctx)

    override def onUpstreamFinish(ctx: Context[ByteString]): TerminationDirective = {
      if (buffer.nonEmpty) ctx.absorbTermination()
      else ctx.finish()
    }

    private def emitChunkOrPull(ctx: Context[ByteString]): SyncDirective = {
      if (buffer.length < chunkSize) {
        if (ctx.isFinishing) ctx.pushAndFinish(buffer)
        else ctx.pull()
      } else {
        val (emit, nextBuffer) = buffer.splitAt(chunkSize)
        buffer = nextBuffer
        ctx.push(emit)
      }
    }
  }

}
