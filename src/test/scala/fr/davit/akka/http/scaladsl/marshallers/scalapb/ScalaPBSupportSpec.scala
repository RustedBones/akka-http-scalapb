package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.javadsl.server.UnacceptedResponseContentTypeRejection
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentType, ContentTypes, MediaRanges, MediaTypes}
import akka.http.scaladsl.server.Directives.{complete, get}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import fr.davit.generated.test.TestMessage
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import scalapb.json4s.JsonFormat

class ScalaPBSupportSpec extends FlatSpec with Matchers with ScalatestRouteTest {


  import ScalaPBSupport._

  trait Fixture {
    val proto = TestMessage("test", 42)
    val json  = JsonFormat.toJsonString(proto)
    val bytes  = TestMessage.toByteArray(proto)
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
