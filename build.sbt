// General info
val username = "RustedBones"
val repo     = "akka-http-scalapb"

// for sbt-github-actions
ThisBuild / crossScalaVersions := Seq("2.13.8", "2.12.16")
ThisBuild / scalaVersion := crossScalaVersions.value.head
ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(name = Some("Check project"), commands = List("scalafmtCheckAll", "headerCheckAll")),
  WorkflowStep.Sbt(name = Some("Build project"), commands = List("test"))
)
ThisBuild / githubWorkflowTargetBranches := Seq("main")
ThisBuild / githubWorkflowPublishTargetBranches := Seq.empty

lazy val commonSettings = Seq(
  organization := "fr.davit",
  organizationName := "Michel Davit",
  homepage := Some(url(s"https://github.com/$username/$repo")),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  startYear := Some(2019),
  scmInfo := Some(ScmInfo(url(s"https://github.com/$username/$repo"), s"git@github.com:$username/$repo.git")),
  developers := List(
    Developer(
      id = s"$username",
      name = "Michel Davit",
      email = "michel@davit.fr",
      url = url(s"https://github.com/$username")
    )
  ),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := Some(if (isSnapshot.value) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  credentials ++= (for {
    username <- sys.env.get("SONATYPE_USERNAME")
    password <- sys.env.get("SONATYPE_PASSWORD")
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq,
  libraryDependencies ++= Seq(
    Dependencies.AkkaHttp,
    Dependencies.ScalaCollectionCompat,
    Dependencies.ScalaPB,
    Dependencies.Provided.AkkaStream,
    Dependencies.Test.AkkaHttpTestkit,
    Dependencies.Test.AkkaTestkit,
    Dependencies.Test.ScalaTest
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
      Dependencies.ScalaPbJson4s
    )
  )
