name := "mud"

version := "0.1"

scalaVersion := "2.10.1"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

// Main
libraryDependencies ++= Seq(
  "org.scalaz" % "scalaz-core_2.10" % "7.0.0",
  "org.scalaz" % "scalaz-effect_2.10" % "7.0.0",
  "org.scala-stm" %% "scala-stm" % "0.7",
  "io.netty" % "netty" % "4.0.0.Alpha8"
)

// Test
libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.specs2" %% "specs2" % "1.13" % "test"
)

// Let's add a linter
resolvers += "linter" at "http://hairyfotr.github.io/linteRepo/releases"

addCompilerPlugin("com.foursquare.lint" %% "linter" % "0.1-SNAPSHOT")

// And turn warnings all the way up
scalacOptions ++= Seq("-Yno-adapted-args", "-Ywarn-all", "-Xfatal-warnings")




