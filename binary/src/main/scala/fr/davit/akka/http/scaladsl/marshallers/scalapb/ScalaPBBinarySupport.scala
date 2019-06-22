package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaType
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}

trait ScalaPBBinarySupport {

  type ProtoMessage[T] = GeneratedMessage with Message[T]

  lazy val protobufMediaType: MediaType.Binary = MediaType.applicationBinary("protobuf", MediaType.NotCompressible)

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPBBinaryUnmarshaller[T <: ProtoMessage[T]](
      implicit gmc: GeneratedMessageCompanion[T]): FromEntityUnmarshaller[T] = {
    Unmarshaller.byteArrayUnmarshaller.forContentTypes(protobufMediaType).map(gmc.parseFrom)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPBBinaryMarshaller[T <: ProtoMessage[T]](
      implicit gmc: GeneratedMessageCompanion[T]): ToEntityMarshaller[T] = {
    Marshaller.ByteArrayMarshaller.wrap(protobufMediaType)(gmc.toByteArray)
  }
}

object ScalaPBBinarySupport extends ScalaPBBinarySupport
