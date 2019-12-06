// General info
val username = "RustedBones"
val repo     = "akka-http-scalapb"

lazy val commonSettings = Seq(
  organization := "fr.davit",
  version := "0.2.2-SNAPSHOT",
  crossScalaVersions := Seq("2.11.12", "2.12.10", "2.13.1"),
  scalaVersion := crossScalaVersions.value.last,
  Compile / compile / scalacOptions ++= Settings.scalacOptions(scalaVersion.value),
  homepage := Some(url(s"https://github.com/$username/$repo")),
  licenses += "APACHE" -> url(s"https://github.com/$username/$repo/blob/master/LICENSE"),
  scmInfo := Some(ScmInfo(url(s"https://github.com/$username/$repo"), s"git@github.com:$username/$repo.git")),
  developers := List(
    Developer(
      id = s"$username",
      name = "Michel Davit",
      email = "michel@davit.fr",
      url = url(s"https://github.com/$username"))
  ),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  credentials ++= (for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq,

  libraryDependencies ++= Seq(
    Dependencies.akkaHttp,
    Dependencies.scalaCollectionCompat,
    Dependencies.scalaPB,
    Dependencies.Provided.akkaStream,
    Dependencies.Test.akkaHttpTestkit,
    Dependencies.Test.akkaTestkit,
    Dependencies.Test.scalaTest
  )
)

lazy val `akka-http-scalapb` = (project in file("."))
  .dependsOn(`akka-http-scalapb-binary`, `akka-http-scalapb-json4s`)
  .aggregate(`akka-http-scalapb-binary`, `akka-http-scalapb-json4s`)
  .settings(commonSettings: _*)
  .settings(ScalaPBSettings.default: _*)

lazy val `akka-http-scalapb-binary` = (project in file("binary"))
  .settings(commonSettings: _*)
  .settings(ScalaPBSettings.default: _*)

lazy val `akka-http-scalapb-json4s` = (project in file("json4s"))
  .settings(commonSettings: _*)
  .settings(ScalaPBSettings.default: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.scalaPbJson4s
    )
  )
