package com.mielientiev.bigfiles.demo5

import akka.stream.{ClosedShape, FlowShape}
import akka.stream.scaladsl._
import com.mielientiev.bigfiles.ImplicitContext

/**
  * Created by ihor-mielientiev on 3/15/16.
  */
object Demo5 extends ImplicitContext {

  class MyComplexClass(val age: Int)
  def main(args: Array[String]) {
    val iter = Stream.fill[MyComplexClass](2)(new MyComplexClass(20))
    val in = Source[MyComplexClass](iter)

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._

      val bcast = builder.add(Broadcast[MyComplexClass](2))
      val zip = builder.add(ZipWith[Int, Int, Int]((x, y) => x + y))

      val f1 = Flow[MyComplexClass].fold[Int](0)((acc, y) => {println(y);acc + y.age})
      val f2 = Flow[MyComplexClass].fold[Int](1)((acc, y) => {println(y);acc * y.age})
      val out = Sink.foreach[Int](println)
      val out2 = Sink.foreach[Int](x=>println("2"))

      in ~> bcast ~> f1 ~> out
            bcast ~> f1 ~> out2


     ClosedShape
    })

    g.run()
  }
}
