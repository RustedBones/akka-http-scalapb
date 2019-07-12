package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import fr.davit.generated.test.TestMessage
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers}

class ScalaPBBinarySupportSpec extends FlatSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  import ScalaPBBinarySupport._

  trait Fixture {
    val proto = TestMessage("test", 42)
    val bytes = TestMessage.toByteArray(proto)
  }

  "ScalaPBJsonSupport" should "marshall proto message to bytes with default content type" in new Fixture {
    Get() ~> get(complete(proto)) ~> check {
      contentType shouldBe (protobufMediaTypes.head: ContentType)
      responseAs[Array[Byte]] shouldBe bytes
    }
  }

  it should "marshall proto message to bytes with requested content type" in new Fixture {
    protobufMediaTypes.foreach { mediaType =>
      Get().withHeaders(Accept(mediaType)) ~> get(complete(proto)) ~> check {
        contentType shouldBe (mediaType: ContentType)
        responseAs[Array[Byte]] shouldBe bytes
      }
    }
  }

  it should "unmarshall bytes to proto message" in new Fixture {
    protobufMediaTypes.foreach { mediaType =>
      val entity = HttpEntity(mediaType, bytes)
      Unmarshal(entity).to[TestMessage].futureValue shouldBe proto
    }
  }

  it should "fail unmarshalling if the content type is not application/protobuf" in new Fixture {
    val entity = HttpEntity(ContentTypes.`application/octet-stream`, bytes)
    Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
  }

}
