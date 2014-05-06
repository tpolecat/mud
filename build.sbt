name := "mud"

version := "0.1"

scalaVersion := "2.10.4"

// Set things up so we can run async
fork := true

cancelable := true

// Main
libraryDependencies ++= Seq(
  "org.scalaz"     % "scalaz-core_2.10"   % "7.0.6",
  "org.scalaz"     % "scalaz-effect_2.10" % "7.0.6",
  "org.scala-stm" %% "scala-stm"          % "0.7",
  "io.netty"       % "netty"              % "4.0.0.Alpha8"
)

// Test
libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck"           % "1.10.0"       % "test",
  "org.specs2"     %% "specs2"               % "1.13"         % "test"
)

// And turn warnings all the way up
scalacOptions ++= Seq(
  "-deprecation",           
  "-encoding", "UTF-8",       // yes, this is 2 args
  "-feature",                
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",       
  "-Xlint",
  "-Yno-adapted-args",       
  "-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen"
//"-Ywarn-value-discard"     
)



