import sbt._

object Dependencies {

  object Versions {
    val akka                  = "2.6.15"
    val akkaHttp              = "10.2.6"
    val scalaCollectionCompat = "2.5.0"
    val scalaPB               = "0.11.5"
    val scalaPBJson4s         = "0.11.1"
    val scalaTest             = "3.2.9"
  }

  val akkaHttp              = "com.typesafe.akka"      %% "akka-http"               % Versions.akkaHttp
  val scalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % Versions.scalaCollectionCompat
  val scalaPB               = "com.thesamet.scalapb"   %% "scalapb-runtime"         % Versions.scalaPB
  val scalaPbJson4s         = "com.thesamet.scalapb"   %% "scalapb-json4s"          % Versions.scalaPBJson4s

  object Provided {
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % Versions.akka % "provided"
  }

  object Test {
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp  % "test"
    val akkaTestkit     = "com.typesafe.akka" %% "akka-testkit"      % Versions.akka      % "test"
    val scalaTest       = "org.scalatest"     %% "scalatest"         % Versions.scalaTest % "test"
  }
}
