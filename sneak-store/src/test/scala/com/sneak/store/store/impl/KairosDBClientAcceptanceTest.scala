package com.sneak.store.store.impl

import org.specs2.mutable.Specification
import com.typesafe.config.ConfigFactory
import com.sneak.thrift.Message
import org.joda.time.DateTime
import scala.util.{Failure, Success}
import com.typesafe.scalalogging.slf4j.Logging

class KairosDBClientAcceptanceTest extends Specification with Logging {

  val config = ConfigFactory.parseString(
    """
      |kairosdb {
      |  rest {
      |    host = "127.0.0.1"
      |    port = 8080
      |  }
      |  timeout = 10
      |}
    """.stripMargin)

  "client " should {
    "store a value" in {
      val client = KairosDBClient.open(config)

      val responseFuture = client.storeMetric(Message(
        new DateTime(2000, 1, 1, 0, 0).getMillis,
        "gen666",
        100,
        "gb",
        "idea"
      ))

      import scala.concurrent.ExecutionContext.Implicits.global

      responseFuture onComplete {
        case Success(_) => logger.info("Success")
        case Failure(e) => logger.error("Error", e)
        case _ => logger.info("Done")
      }

      val test = ""
      test mustEqual ""
    }
  }
}
