name := """cp-core"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "com.dropbox.core" % "dropbox-core-sdk" % "1.7.4",
  "com.google.http-client" % "google-http-client-jackson2" % "1.14.1-beta",
  "org.json" % "json" % "20140107"
)