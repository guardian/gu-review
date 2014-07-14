name := """gu-review"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "com.amazonaws" % "aws-java-sdk" % "1.8.4"
)
