import dependencies.Dependencies._
import settings.Scopes._

val figlet4sVersion = "0.1.0"

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.colofabrix.scala.figlet4s"
ThisBuild / scalaVersion := ScalaLangVersion
ThisBuild / turbo := true
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "@ColOfAbRiX", url("http://github.com/ColOfAbRiX")),
)

// Compiler options
ThisBuild / scalacOptions := Compiler.TpolecatOptions_2_13 ++ Compiler.StrictOptions ++ Seq("-P:splain:all")
ThisBuild / Test / scalacOptions := Compiler.TpolecatOptions_2_13 ++ Seq("-P:splain:all")

// Global dependencies and compiler plugins
ThisBuild / libraryDependencies ++= Seq(SplainPlugin, WartremoverPlugin)

// Wartremover
ThisBuild / wartremoverExcluded ++= (baseDirectory.value * "**" / "src" / "test").get
ThisBuild / wartremoverErrors ++= Warts.allBut(
  Wart.Any,
  Wart.DefaultArguments,
  Wart.Nothing,
  Wart.Overloading,
  Wart.StringPlusAny,
  Wart.ToString,
  // Covered by ScalaFix
  Wart.PublicInference
)

// Scalafmt
ThisBuild / scalafmtOnCompile := true

// Scalafix
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.0"
ThisBuild / scalafixOnCompile := true
ThisBuild / scalafixScalaBinaryVersion := ScalaLangVersion.replaceAll("\\.\\d+$", "")
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

// Figlet4s
lazy val figlet4s: Project = project
  .in(file("."))
  .aggregate(figlet4sCore, figlet4sEffects, figlet4sBenchmarks)
  .settings(
    name := "figlet4s"
  )

// Figlet4s Core project
lazy val figlet4sCore: Project = project
  .in(file("figlet4s-core"))
  .settings(
    name := "figlet4s-core",
    description := "Scala FIGlet implementation",
    version := figlet4sVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      EnumeratumDep,
      ScalaTestFlatSpecDep,
      ScalaTestPlusCheckDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Effects project
lazy val figlet4sEffects: Project = project
  .in(file("figlet4s-effects"))
  .dependsOn(figlet4sCore)
  .settings(
    name := "figlet4s-effects",
    description := "Effects extension for Figlet4s",
    version := figlet4sVersion,
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      ScalaTestFlatSpecDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Benchmarks project
lazy val figlet4sBenchmarks: Project = project
  .in(file("figlet4s-benchmarks"))
  .configs(Benchmark)
  .dependsOn(figlet4sCore)
  .settings(
    name := "figlet4s-benchmarks",
    description := "Benchmarks for Figlet4s",
    version := figlet4sVersion,
    resolvers += SonatypeRepo,
    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    logBuffered := false,
    parallelExecution in Test := false,
    // inConfig(Benchmark)(Defaults.testSettings),
    libraryDependencies ++= Seq(
      CatsCoreDep,
      ScalameterDep,
    ),
  )
