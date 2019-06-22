package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.scaladsl.model.{ContentType, ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import fr.davit.generated.test.TestMessage
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class ScalaPBBinarySupportSpec
  extends FlatSpec
    with Matchers
    with ScalaFutures
    with ScalatestRouteTest
    with BeforeAndAfterAll {

  import ScalaPBBinarySupport._

  trait Fixture {
    val proto = TestMessage("test", 42)
    val bytes  = TestMessage.toByteArray(proto)
  }

  override def afterAll(): Unit = {
    cleanUp()
    super.afterAll()
  }

  "ScalaPBJsonSupport" should "marshall proto message to bytes" in new Fixture {
    Get() ~> get(complete(proto)) ~> check {
      contentType shouldBe (protobufMediaType: ContentType)
      responseAs[Array[Byte]] shouldBe bytes
    }
  }

  it should "unmarshall bytes to proto message" in new Fixture {
    val entity = HttpEntity(protobufMediaType, bytes)
    Unmarshal(entity).to[TestMessage].futureValue shouldBe proto
  }

  it should "fail unmarshalling if the content type is not application/protobuf" in new Fixture {
    val entity = HttpEntity(ContentTypes.`application/octet-stream`, bytes)
    Unmarshal(entity).to[TestMessage].failed.futureValue shouldBe an[UnsupportedContentTypeException]
  }

}
