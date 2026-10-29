ThisBuild / scalaVersion := "3.3.7"
ThisBuild / version      := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "criptoclave",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "org.scalameta"          %% "munit"       % "1.0.0" % Test
    ),
    // Evita problemas con la ventana al ejecutar con sbt run
    run / fork := true
  )
