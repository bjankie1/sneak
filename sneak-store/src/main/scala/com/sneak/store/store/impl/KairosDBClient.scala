package com.sneak.store.store.impl

import scala.concurrent.Future
import scala.concurrent.duration._

import com.sneak.store.store.MetricsStore
import com.sneak.thrift.Message

import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._
import spray.can.Http

import com.typesafe.config.Config
import akka.pattern.ask
import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import spray.httpx.SprayJsonSupport._

/**
 * Client to KairosDB database.
 */
class KairosDBClient(implicit config: Config) extends MetricsStore {

  val restHost = config.getString("kairosdb.rest.host")
  val restPort = config.getInt("kairosdb.rest.port")

  implicit val timeout = Timeout(config.getInt("kairosdb.timeout") seconds)


  // execution context for futures

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  import KairosDBProtocol._

  val pipeline: Future[SendReceive] =
    for (
      Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup(host = restHost, port = restPort)
    ) yield sendReceive(connector)

  /**
   * Persists the message in a persistent storage.
   *
   * @param metric
   */
  override def storeMetric(metric: Message): Future[_] = {
    storeMetrics(List(metric))
  }

  /**
   * Store metrics in batch manner.
   *
   * @param metrics Set of metrics
   * @return
   */
  override def storeMetrics(metrics: Iterable[Message]): Future[_] = {
    pipeline.flatMap(_(Post("/api/v1/datapoints", metrics)))
  }
}

object KairosDBClient {
  def open(implicit config: Config): KairosDBClient = {
    new KairosDBClient
  }
}
