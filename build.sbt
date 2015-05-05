name := "SentimentAnalysisT3"

version := "1.0"

scalaVersion := "2.11.6"



libraryDependencies ++= {
  Seq(
    "org.scalaj" %% "scalaj-http" % "1.1.4",
    "com.typesafe.play" % "play-json_2.11" % "2.4.0-M3",
    "org.scalaz" %% "scalaz-core" % "7.1.2",
    "org.scalaz" %% "scalaz-effect" % "7.1.2",
    "org.scalaz" %% "scalaz-typelevel" % "7.1.2",
    "org.scalaz" %% "scalaz-scalacheck-binding" % "7.1.2" % "test"
  )
}