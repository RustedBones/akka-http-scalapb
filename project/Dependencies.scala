import sbt._

object Dependencies {

  object Versions {
    val akka          = "2.5.19"
    val akkaHttp      = "10.1.7"
    val scalaTest     = "3.0.5"
    val scalaPB       = "0.8.4"
    val scalaPBJson4s = "0.7.2"
  }

  val akkaHttp      = "com.typesafe.akka"    %% "akka-http"       % Versions.akkaHttp
  val scalaPB       = "com.thesamet.scalapb" %% "scalapb-runtime" % Versions.scalaPB
  val scalaPbJson4s = "com.thesamet.scalapb" %% "scalapb-json4s"  % Versions.scalaPBJson4s

  object Provided {
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % Versions.akka % "provided"
  }

  object Test {
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp  % "test"
    val akkaTestkit     = "com.typesafe.akka" %% "akka-testkit"      % Versions.akka      % "test"
    val scalaTest       = "org.scalatest"     %% "scalatest"         % Versions.scalaTest % "test"
  }
}
