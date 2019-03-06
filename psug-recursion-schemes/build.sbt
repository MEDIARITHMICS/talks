name := "psug-droste-castel"

version := "0.1"

scalaVersion := "2.12.8"
val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "1.2.0",
  "com.pepegar" %% "hammock-core" % "0.8.5",
  "com.pepegar" %% "hammock-asynchttpclient" % "0.8.5",
  "com.pepegar" %% "hammock-circe" % "0.8.5",
  "io.higherkindness" %% "droste-core" % "0.6.0",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-generic-extras" % circeVersion,
)

scalacOptions ++= Seq(
  "-Ypartial-unification"
)