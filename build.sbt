
lazy val root = (project in file(".")).
  settings(
    name := "ManageBigFilesTalk",
    version := "1.0.0",
    scalaVersion := "2.11.8",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("snapshots"),
      "Atlassian Releases" at "https://maven.atlassian.com/public/",
      "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
    ),

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream-experimental" % "2.0.3",
      "com.typesafe.akka" %% "akka-http-core-experimental" % "2.0.3",
      "com.typesafe.akka" %% "akka-http-experimental" % "2.0.3",
      "com.amazonaws" % "aws-java-sdk-s3" % "1.10.60",
      "com.google.inject" % "guice" % "4.0",
      "net.codingwell" %% "scala-guice" % "4.0.1"
    ),
      scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint", // Enable recommended additional warnings.
      "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
      "-encoding", "UTF-8",
      "-Xexperimental" // Add experimental scala feature
      )
  )



