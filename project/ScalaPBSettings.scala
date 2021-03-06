import sbt._
import sbt.Keys._
import sbtprotoc.ProtocPlugin.autoImport._

object ScalaPBSettings {

  val default: Seq[SettingsDefinition] = {
    (inConfig(Test)(sbtprotoc.ProtocPlugin.protobufConfigSettings): SettingsDefinition) +:
      Seq(Compile, Test).map { configuration =>
        configuration / PB.targets := Seq(
          scalapb.gen(grpc = false) -> (configuration / sourceManaged).value / "protobuf"
        )
      }
  }
}
