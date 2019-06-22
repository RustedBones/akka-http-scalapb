package fr.davit.akka.http.scaladsl.marshallers.scalapb

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import org.json4s.JsonAST.{JArray, JValue}
import org.json4s.jackson.JsonMethods._
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message}
import scalapb.json4s.{JsonFormatException, Parser, Printer}

import scala.collection.compat._

trait ScalaPBJsonSupport {

  type ProtoMessage[T] = GeneratedMessage with Message[T]

  protected lazy val printer = new Printer()
  protected lazy val parser = new Parser()

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  private val json4sUnmarshaller: FromEntityUnmarshaller[JValue] = {
    Unmarshaller.stringUnmarshaller.forContentTypes(MediaTypes.`application/json`).map(parse(_))
  }

  implicit def scalaPBJsonUnmarshaller[T <: ProtoMessage[T]: GeneratedMessageCompanion]: FromEntityUnmarshaller[T] = {
    json4sUnmarshaller.map(parser.fromJson[T])
  }

  // scalapb doesn't offer the possibility to have an array as Json root object, but this can be supported by Json
  implicit def scalaPBJsonCollUnmarshaller[T <: ProtoMessage[T]: GeneratedMessageCompanion, CC[_]](
      implicit factory: Factory[T, CC[T]]): FromEntityUnmarshaller[CC[T]] = {
    json4sUnmarshaller.map {
      case JArray(values) => values.iterator.map(parser.fromJson[T]).to(factory)
      case value          => throw new JsonFormatException(s"Expected an array, found $value")
    }
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  private val json4sMarshaller: ToEntityMarshaller[JValue] = {
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`)(compact)
  }

  implicit final def scalaPBJsonMarshaller[T <: ProtoMessage[T]]: ToEntityMarshaller[T] = {
    json4sMarshaller.compose(printer.toJson)
  }

  // scalapb doesn't offer the possibility to have an array as Json root object, but this can be supported by Json
  implicit final def scalaPBJsonCollMarshaller[T <: ProtoMessage[T]]: ToEntityMarshaller[Iterable[T]] = {
    json4sMarshaller.compose(protos => JArray(protos.map(printer.toJson).toList))
  }
}

object ScalaPBJsonSupport extends ScalaPBJsonSupport
