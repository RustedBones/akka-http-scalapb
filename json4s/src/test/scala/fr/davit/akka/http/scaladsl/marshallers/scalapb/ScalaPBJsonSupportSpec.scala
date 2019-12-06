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

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import fr.davit.generated.test.TestMessage
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import scalapb.json4s.JsonFormat

class ScalaPBJsonSupportSpec extends FlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  import ScalaPBJsonSupport._

  trait Fixture {
    val proto = TestMessage("test", 42)
    val json  = JsonFormat.toJsonString(proto)
  }

  trait ArrayFixture {
    val proto = Seq(TestMessage("value 1", 1), TestMessage("value 2", 2))
    val json  = proto.map(JsonFormat.toJsonString).mkString("[", ",", "]")
  }

  "ScalaPBJsonSupport" should "marshall proto message to json" in new Fixture {
    Get() ~> get(complete(proto)) ~> check {
      contentType shouldBe ContentTypes.`application/json`
      responseAs[String] shouldBe json
    }
  }

  it should "marshall proto message collections to json" in new ArrayFixture {
    Get() ~> get(complete(proto)) ~> check {
      contentType shouldBe ContentTypes.`application/json`
      responseAs[String] shouldBe json
    }
  }

  it should "unmarshall json to proto message" in new Fixture {
    val entity = HttpEntity(ContentTypes.`application/json`, json)
    Unmarshal(entity).to[TestMessage].futureValue shouldBe proto
  }

  it should "unmarshall json array to collections of proto messages" in new ArrayFixture {
    val entity = HttpEntity(ContentTypes.`application/json`, json)
    Unmarshal(entity).to[List[TestMessage]].futureValue should contain theSameElementsAs proto
  }

  it should "fail unmarshalling if the content type is not application/json" in new Fixture {
    val entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, json)
    Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
  }

}
