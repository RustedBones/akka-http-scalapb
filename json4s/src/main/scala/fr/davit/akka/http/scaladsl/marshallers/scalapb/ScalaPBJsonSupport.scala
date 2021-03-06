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

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import org.json4s.JsonAST.{JArray, JValue}
import org.json4s.jackson.JsonMethods._
import scalapb.json4s.{JsonFormatException, Parser, Printer}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

import scala.collection.compat._

trait ScalaPBJsonSupport {
  protected lazy val printer = new Printer()
  protected lazy val parser  = new Parser()

  //--------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  //--------------------------------------------------------------------------------------------------------------------
  private val json4sUnmarshaller: FromEntityUnmarshaller[JValue] = {
    Unmarshaller.stringUnmarshaller.forContentTypes(MediaTypes.`application/json`).map(parse(_))
  }

  implicit def scalaPBJsonUnmarshaller[T <: GeneratedMessage: GeneratedMessageCompanion]: FromEntityUnmarshaller[T] = {
    json4sUnmarshaller.map(parser.fromJson[T])
  }

  // scalapb doesn't offer the possibility to have an array as Json root object, but this can be supported by Json
  implicit def scalaPBJsonCollUnmarshaller[T <: GeneratedMessage: GeneratedMessageCompanion, CC[_]](implicit
      factory: Factory[T, CC[T]]
  ): FromEntityUnmarshaller[CC[T]] = {
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

  implicit final def scalaPBJsonMarshaller[T <: GeneratedMessage]: ToEntityMarshaller[T] = {
    json4sMarshaller.compose(printer.toJson)
  }

  // scalapb doesn't offer the possibility to have an array as Json root object, but this can be supported by Json
  implicit final def scalaPBJsonCollMarshaller[T <: GeneratedMessage]: ToEntityMarshaller[Iterable[T]] = {
    json4sMarshaller.compose(protos => JArray(protos.map(printer.toJson).toList))
  }
}

object ScalaPBJsonSupport extends ScalaPBJsonSupport
