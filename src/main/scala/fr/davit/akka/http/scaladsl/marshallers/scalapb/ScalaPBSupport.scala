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
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

trait ScalaPBSupport {

  // --------------------------------------------------------------------------------------------------------------------
  // Unmarshallers
  // --------------------------------------------------------------------------------------------------------------------
  implicit def scalaPbUnmarshaller[T <: GeneratedMessage: GeneratedMessageCompanion]: FromEntityUnmarshaller[T] = {
    Unmarshaller.firstOf(ScalaPBJsonSupport.scalaPBJsonUnmarshaller, ScalaPBBinarySupport.scalaPBBinaryUnmarshaller)
  }

  // --------------------------------------------------------------------------------------------------------------------
  // Marshallers
  // --------------------------------------------------------------------------------------------------------------------
  implicit def scalaPbMarshaller[T <: GeneratedMessage: GeneratedMessageCompanion]: ToEntityMarshaller[T] = {
    Marshaller.oneOf(ScalaPBJsonSupport.scalaPBJsonMarshaller, ScalaPBBinarySupport.scalaPBBinaryMarshaller)
  }
}

object ScalaPBSupport extends ScalaPBSupport
