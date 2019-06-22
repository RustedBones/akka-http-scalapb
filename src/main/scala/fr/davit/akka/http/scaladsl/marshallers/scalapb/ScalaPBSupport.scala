package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}

trait ScalaPBSupport {

  type ProtoMessage[T] = GeneratedMessage with Message[T]

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPbUnmarshaller[T <: ProtoMessage[T]: GeneratedMessageCompanion]: FromEntityUnmarshaller[T] = {
    Unmarshaller.firstOf(ScalaPBJsonSupport.scalaPBJsonUnmarshaller, ScalaPBBinarySupport.scalaPBBinaryUnmarshaller)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPbMarshaller[T <: ProtoMessage[T]: GeneratedMessageCompanion]: ToEntityMarshaller[T] = {
    Marshaller.oneOf(ScalaPBJsonSupport.scalaPBJsonMarshaller, ScalaPBBinarySupport.scalaPBBinaryMarshaller)
  }

}

object ScalaPBSupport extends ScalaPBSupport
