package com.sneak.store.store.impl

import spray.json._
import com.sneak.thrift.Message
import scala.collection.immutable.Map


object KairosDBProtocol extends DefaultJsonProtocol {

  implicit object KairosDBJsonFormat extends RootJsonFormat[Message] {
    def write(msg: Message): JsObject = {
      val tags: Map[String, JsValue] = msg.options.toMap.mapValues[JsValue](v => JsString(v)) +
                 ("host" -> JsString(msg.host)) +
                 ("application" -> JsString(msg.application))
      JsObject(
        "name" -> JsString(msg.name),
        "datapoints" -> JsArray(JsArray(JsNumber(msg.timestamp), JsNumber(msg.value))),
        "tags" -> JsObject(tags)
      )
    }

    def read(value: JsValue) = {
      value.asJsObject.getFields("name", "datapoints", "tags") match {
        case Seq(JsString(name), JsNumber(value), JsString(tags)) =>
          Message(0l, name, value.toDouble, null, null, null)
        case _ => throw new DeserializationException("Message expected")
      }
    }
  }
}