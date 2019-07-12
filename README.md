# akka-http-scalapb

[![Build Status](https://travis-ci.org/RustedBones/akka-http-scalapb.svg?branch=master&style=flat)](https://travis-ci.org/RustedBones/akka-http-scalapb)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/fr.davit/akka-http-scalapb_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/fr.davit/akka-http-scalapb_2.12)
[![Software License](https://img.shields.io/badge/license-Apache%202-brightgreen.svg?style=flat)](LICENSE)

akka-http protobuf and json marshalling/unmarshalling for ScalaPB messages


## Versions

| Version | Release date | Akka Http version | ScalaPB version          | Scala versions                |
| ------- | ------------ | ----------------- | ------------------------ | ----------------------------- |
| `0.1.0` | 2019-01-27   | `10.1.7`          | `0.8.4` (`0.7.2` json4s) | `2.11.12`, `2.12.8`           |
| `0.2.0` | 2019-06-22   | `10.1.8`          | `0.9.0` (`0.9.2` json4s) | `2.11.12`, `2.12.8`, `2.13.0` |

The complete list can be found in the [CHANGELOG](CHANGELOG.md) file.

## Getting akka-http-scalapb

Libraries are published to Maven Central. Add to your `build.sbt`:

```scala
libraryDependencies += "fr.davit" %% "akka-http-scalapb"        % <version> // binary & json support
```

**Important**: Since akka-http 10.1.0, akka-stream transitive dependency is marked as provided. You should now explicitly
include it in your build.

> [...] we changed the policy not to depend on akka-stream explicitly anymore but mark it as a provided dependency in our build. 
That means that you will always have to add a manual dependency to akka-stream. Please make sure you have chosen and 
added a dependency to akka-stream when updating to the new version

```scala
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % <version> // Only Akka 2.5 supported
```

For more details, see the akka-http 10.1.x [release notes](https://doc.akka.io/docs/akka-http/current/release-notes/10.1.x.html)

## Quick start

For the examples, we are using the following proto domain model 

```proto
message Item {
  string name = 1;
  int64 id    = 2;
}

message Order {
  reapeated Item items = 1;
}
```

The implicit marshallers and unmarshallers for your generated proto classes are defined in `ScalaPBSupport`. You
simply need to have them in scope.

```scala
import akka.http.scaladsl.server.Directives
import fr.davit.akka.http.scaladsl.marshallers.scalapb.ScalaPBSupport


class MyProtoService extends Directives with ScalaPBSupport {

  // format: OFF
  val route =
    get {
      pathSingleSlash {
        complete(Item("thing", 42))
      }
    } ~
    post {
      entity(as[Order]) { order =>
        val itemsCount = order.items.size
        val itemNames = order.items.map(_.name).mkString(", ")
        complete(s"Ordered $itemsCount items: $itemNames")
      }
    }
  // format: ON
}
```

Marshalling/Unmarshalling of the generated classes depends on the `Accept`/`Content-Type` header sent by the client:
- `Content-Type: application/json`: json
- `Content-Type: application/x-protobuf`: binary
- `Content-Type: application/x-protobuffer`: binary
- `Content-Type: application/protobuf`: binary
- `Content-Type: application/vnd.google.protobuf`: binary

No `Accept` header or matching several (eg `Accept: application/*`) will take the 1st matching type from the above list.


### Json only

If you are using scalaPB for json (un)marshalling only, you can use `ScalaPBJsonSupport` from the sub module

```scala
libraryDependencies += "fr.davit" %% "akka-http-scalapb-json4s"        % <version> // json support only
```

### Binary only 

If you are using scalaPB for binary (un)marshalling only, you can use `ScalaPBBinarySupport` form the sub module

```scala
libraryDependencies += "fr.davit" %% "akka-http-scalapb-binary"        % <version> // binary support only
```

## Limitation

Only json (un)marshallers are able to support collections of proto messages as root object.

Entity streaming (http chunked transfer) is at the moment not supported by the library.