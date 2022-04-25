import sbt._

object Dependencies {

  object Versions {
    val Akka                  = "2.6.19"
    val AkkaHttp              = "10.2.9"
    val ScalaCollectionCompat = "2.7.0"
    val ScalaPB               = "0.11.10"
    val ScalaPBJson4s         = "0.12.0"
    val ScalaTest             = "3.2.12"
  }

  val AkkaHttp              = "com.typesafe.akka"      %% "akka-http"               % Versions.AkkaHttp
  val ScalaCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % Versions.ScalaCollectionCompat
  val ScalaPB               = "com.thesamet.scalapb"   %% "scalapb-runtime"         % Versions.ScalaPB
  val ScalaPbJson4s         = "com.thesamet.scalapb"   %% "scalapb-json4s"          % Versions.ScalaPBJson4s

  object Provided {
    val AkkaStream = "com.typesafe.akka" %% "akka-stream" % Versions.Akka % "provided"
  }

  object Test {
    val AkkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Versions.AkkaHttp  % "test"
    val AkkaTestkit     = "com.typesafe.akka" %% "akka-testkit"      % Versions.Akka      % "test"
    val ScalaTest       = "org.scalatest"     %% "scalatest"         % Versions.ScalaTest % "test"
  }
}
