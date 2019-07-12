package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypeRange, MediaType}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}

trait ScalaPBBinarySupport {

  type ProtoMessage[T] = GeneratedMessage with Message[T]

  /**
    * There is no official media type for protocol buffers registered
    * those are some of the more popular choices being used today, x-protobuf being the default for the google-http-client
    * see https://googleapis.dev/java/google-http-client/latest/com/google/api/client/protobuf/ProtocolBuffers.html
    */
  lazy val protobufMediaTypes: Seq[MediaType.Binary] = {
    List("x-protobuf", "x-protobuffer", "protobuf", "vnd.google.protobuf").map { t =>
      MediaType.applicationBinary(t, MediaType.NotCompressible)
    }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPBBinaryUnmarshaller[T <: ProtoMessage[T]](
      implicit gmc: GeneratedMessageCompanion[T]): FromEntityUnmarshaller[T] = {
    Unmarshaller.byteArrayUnmarshaller
      .forContentTypes(protobufMediaTypes.map(ContentTypeRange.apply): _*)
      .map(gmc.parseFrom)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPBBinaryMarshaller[T <: ProtoMessage[T]](
      implicit gmc: GeneratedMessageCompanion[T]): ToEntityMarshaller[T] = {
    Marshaller.oneOf(protobufMediaTypes.map(Marshaller.ByteArrayMarshaller.wrap(_)(gmc.toByteArray)): _*)
  }
}

object ScalaPBBinarySupport extends ScalaPBBinarySupport
