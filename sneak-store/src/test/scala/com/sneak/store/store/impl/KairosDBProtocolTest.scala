package com.sneak.store.store.impl

import org.specs2.mutable.Specification
import spray.json.JsObject
import com.sneak.thrift.Message
import KairosDBProtocol._
import com.sneak.store._
import org.specs2.ScalaCheck
import org.scalacheck.Arbitrary

class KairosDBProtocolTest extends Specification with ScalaCheck {

  "message" should {
    "be encoded" in {
      implicit val arb = Arbitrary {msgGenerator}
      check((msg: Message) => {
        val jsObject: JsObject = KairosDBProtocol.KairosDBJsonFormat.write(msg)
        jsObject.fields.keys must contain("name")
      })
    }
  }
}
