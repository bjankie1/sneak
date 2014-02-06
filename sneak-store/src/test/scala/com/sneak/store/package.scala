package com.sneak

import org.scalacheck.{Arbitrary, Gen}
import com.sneak.thrift.Message
import org.scalacheck.Arbitrary._
import scala.annotation.tailrec

/**
 * Various generators
 */
package object store {

  val genIntList      = Gen.containerOf[List,Int](Gen.oneOf(1, 3, 5))


  implicit val msgGenerator: Gen[Message] = for {
    time <- arbitrary[Long].suchThat( _ > 0)
    name <- Gen.oneOf("metric1", "metric2", "metric3")
    value <- Gen.choose(0, 100)
    host <- Gen.oneOf("host1", "host2")
    application <- Gen.oneOf("app1", "app2")
  } yield Message(time, name, value, host, application)

  implicit def msgSequenceGenerator(n: Int) = Gen.containerOfN[Seq, Message](n, msgGenerator)

  /**
   * Picks a random value until generator returns something different than None.
   * @param gen Generator
   * @tparam V Result type
   * @return Returns an arbitrary value
   */
  @tailrec
  implicit def randomVal[V](gen: Gen[V]): V = {
    gen.sample match {
      case None => randomVal(gen)
      case Some(value) => value
    }
  }

}
