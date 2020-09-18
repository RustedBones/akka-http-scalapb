/*
 * Copyright 2019 Michel Davit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.javadsl.server.UnacceptedResponseContentTypeRejection
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentType, ContentTypes, MediaRanges, MediaTypes}
import akka.http.scaladsl.server.Directives.{complete, get}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import fr.davit.generated.test.TestMessage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalapb.json4s.JsonFormat

class ScalaPBSupportSpec extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  import ScalaPBSupport._

  trait Fixture {
    val proto = TestMessage("test", 42)
    val json  = JsonFormat.toJsonString(proto)
    val bytes = TestMessage.toByteArray(proto)
  }

  "ScalaPbSupport" should "marshall in json by default" in new Fixture {
    Get() ~> get(complete(proto)) ~> check {
      contentType shouldBe ContentTypes.`application/json`
      responseAs[String] shouldBe json
    }
  }

  it should "marshall in json by when range match both types" in new Fixture {
    Get().withHeaders(Accept(MediaRanges.`application/*`)) ~> get(complete(proto)) ~> check {
      contentType shouldBe ContentTypes.`application/json`
      responseAs[String] shouldBe json
    }
  }

  it should "marshall in json when requested" in new Fixture {
    Get().withHeaders(Accept(MediaTypes.`application/json`)) ~> get(complete(proto)) ~> check {
      contentType shouldBe ContentTypes.`application/json`
      responseAs[String] shouldBe json
    }
  }

  it should "marshall in binary when requested" in new Fixture {
    ScalaPBBinarySupport.protobufMediaTypes.foreach { mediaType =>
      Get().withHeaders(Accept(mediaType)) ~> get(complete(proto)) ~> check {
        contentType shouldBe (mediaType: ContentType)
        responseAs[Array[Byte]] shouldBe bytes
      }
    }
  }

  it should "fail when Accept doesnt' match supported type" in new Fixture {
    Get().withHeaders(Accept(MediaTypes.`text/html`)) ~> get(complete(proto)) ~> check {
      rejection shouldBe a[UnacceptedResponseContentTypeRejection]
    }
  }
}
