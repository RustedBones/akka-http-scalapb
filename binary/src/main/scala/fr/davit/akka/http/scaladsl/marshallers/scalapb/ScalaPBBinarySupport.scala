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
import akka.http.scaladsl.model.{ContentTypeRange, MediaType}
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

trait ScalaPBBinarySupport {

  /** There is no official media type for protocol buffers registered
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
  implicit def scalaPBBinaryUnmarshaller[T <: GeneratedMessage](implicit
      gmc: GeneratedMessageCompanion[T]
  ): FromEntityUnmarshaller[T] = {
    Unmarshaller.byteArrayUnmarshaller
      .forContentTypes(protobufMediaTypes.map(ContentTypeRange.apply): _*)
      .map(gmc.parseFrom)
  }

  //--------------------------------------------------------------------------------------------------------------------
  // Marshallers
  //--------------------------------------------------------------------------------------------------------------------
  implicit def scalaPBBinaryMarshaller[T <: GeneratedMessage](implicit
      gmc: GeneratedMessageCompanion[T]
  ): ToEntityMarshaller[T] = {
    Marshaller.oneOf(protobufMediaTypes.map(Marshaller.ByteArrayMarshaller.wrap(_)(gmc.toByteArray)): _*)
  }
}

object ScalaPBBinarySupport extends ScalaPBBinarySupport
