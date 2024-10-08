import Dependencies._
import TestDependencies._
import sbt._
import utils._
import xerial.sbt.Sonatype._

// General
Global / onChangedBuildSource := ReloadOnSourceChanges
Global / lintUnusedKeysOnLoad := false
ThisBuild / turbo             := true
ThisBuild / scalaVersion      := ScalaLangVersion

// Project information
ThisBuild / name                 := "figlet4s"
ThisBuild / startYear            := Some(2020)
ThisBuild / homepage             := Some(url("https://github.com/ColOfAbRiX/figlet4s"))
ThisBuild / organization         := "com.colofabrix.scala"
ThisBuild / organizationName     := "ColOfAbRiX"
ThisBuild / organizationHomepage := Some(url("https://github.com/ColOfAbRiX"))
ThisBuild / licenses             := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/ColOfAbRiX/figlet4s"), "scm:git@github.com:ColOfAbRiX/figlet4s.git"),
)
ThisBuild / developers := List(
  Developer("ColOfAbRiX", "Fabrizio Colonna", "colofabrix@tin.it", url("http://github.com/ColOfAbRiX")),
)

// Publishing
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle    := true
ThisBuild / sonatypeProjectHosting := Some(
  GitHubHosting("ColOfAbRiX", "figlet4s", "colofabrix@tin.it"),
)
ThisBuild / publishTo := Some(
  if (isSnapshot.value) Opts.resolver.sonatypeSnapshots
  else Opts.resolver.sonatypeStaging,
)

// GIT version information
ThisBuild / dynverSonatypeSnapshots := true

// Scalafix
ThisBuild / scalafixDependencies       += "com.github.liancheng" %% "organize-imports" % "0.4.4"
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision

addCommandAlias("styleApply", "; scalafmtAll; scalafixAll")
addCommandAlias("styleCheck", "; scalafmtCheckAll; scalafixAll --check")

val commonScalaSettings: Seq[Def.Setting[_]] = Seq(
  // Testing
  Test / testOptions += Tests.Argument("-oFD"),
  // Compiler options
  scalacOptions := versioned(scalaVersion.value)(
    Seq.empty,
    Compiler.Options_2_13,
  ),
  Test / scalacOptions := versioned(scalaVersion.value)(
    Seq.empty,
    Compiler.Options_2_13,
  ),
  IntegrationTest / scalacOptions := versioned(scalaVersion.value)(
    Seq.empty,
    Compiler.Options_2_13,
  ),
  // Cross Scala Versions
  crossScalaVersions := SupportedScalaLangVersion,
  // Wartremover
  Compile / wartremoverErrors := Warts.allBut(
    Wart.Any,
    Wart.DefaultArguments,
    Wart.Nothing,
    Wart.Overloading,
    Wart.StringPlusAny,
    Wart.ToString,
    // Covered by ScalaFix
    Wart.PublicInference,
  ),
  // Scaladoc
  Compile / autoAPIMappings := true,
  Compile / doc / scalacOptions ++= Seq(
    "-doc-title",
    "Figlet4s API Documentation",
    "-doc-version",
    version.value,
    "-encoding",
    "UTF-8",
  ),
  // Packaging and publishing
  Compile / packageBin / packageOptions ++= Seq(
    Package.ManifestAttributes(
      ("Git-Build-Branch", git.gitCurrentBranch.value),
      ("Git-Head-Commit-Date", git.gitHeadCommitDate.value.getOrElse("")),
      ("Git-Head-Commit", git.gitHeadCommit.value.getOrElse("")),
      ("Git-Uncommitted-Changes", git.gitUncommittedChanges.value.toString),
    ),
  ),
)

// Figlet4s
lazy val figlet4s: Project = project
  .in(file("."))
  .aggregate(figlet4sCore, figlet4sEffects, figlet4sJava)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(
    name               := "figlet4s",
    crossScalaVersions := Nil,
    publish / skip     := true,
    onLoadMessage :=
      """
        |    _____ _       _      _   _  _
        |   |  ___(_) __ _| | ___| |_| || |  ___
        |   | |_  | |/ _` | |/ _ \ __| || |_/ __|
        |   |  _| | | (_| | |  __/ |_|__   _\__ \
        |   |_|   |_|\__, |_|\___|\__|  |_| |___/
        |            |___/
        |     Welcome to the build for Figlet4s
        |
        |""".stripMargin,
    ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject -- inProjects(figlet4sJava),
  )

// Figlet4s Core project
lazy val figlet4sCore: Project = project
  .in(file("figlet4s-core"))
  .settings(commonScalaSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name        := "figlet4s-core",
    description := "ASCII-art banners in Scala",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep,
      CatsScalaTestDep,
      EnumeratumDep,
      ScalaMockDep,
      ScalaTestFlatSpecDep,
      ScalaTestPlusCheckDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Effects project
lazy val figlet4sEffects: Project = project
  .in(file("figlet4s-effects"))
  .dependsOn(figlet4sCore % "compile->compile;test->test;it->it")
  .settings(commonScalaSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name        := "figlet4s-effects",
    description := "Effects extension for Figlet4s",
    libraryDependencies ++= Seq(
      CatsCoreDep,
      CatsEffectDep,
      CatsKernelDep % Runtime,
      CatsScalaTestDep,
      ScalaTestFlatSpecDep,
      ScalaTestShouldMatchersDep,
    ),
  )

// Figlet4s Java integration project
lazy val figlet4sJava: Project = project
  .in(file("figlet4s-java"))
  .dependsOn(figlet4sCore % "compile->compile;test->test")
  .settings(
    name                         := "figlet4s-java",
    description                  := "Java integration for Figlet4s",
    javacOptions                ++= Compiler.JavacOptions,
    Compile / doc / javacOptions := Seq(),
    crossPaths                   := false,
    libraryDependencies ++= Seq(
      ScalaTestFlatSpecDep,
      ScalaTestShouldMatchersDep,
    ),
  )

lazy val figlet4sMicrosite = project
  .in(file("figlet4s-microsite"))
  .enablePlugins(MicrositesPlugin)
  .settings(
    crossScalaVersions           := SupportedScalaLangVersion,
    micrositeAnalyticsToken      := "UA-189728436-1",
    micrositeAuthor              := "ColOfAbRiX",
    micrositeBaseUrl             := "figlet4s",
    micrositeDescription         := "ASCII-art banners in Scala",
    micrositeDocumentationUrl    := "docs/",
    micrositeGithubOwner         := "ColOfAbRiX",
    micrositeGithubRepo          := "figlet4s",
    micrositeGithubToken         := sys.env.get("GITHUB_TOKEN"),
    micrositeGitterChannel       := false,
    micrositeHighlightLanguages ++= Seq("xml", "plaintext"),
    micrositeHighlightTheme      := "atom-one-dark", // https://highlightjs.org/static/demo/
    micrositeHomepage            := "https://colofabrix.github.io/",
    micrositeName                := "Figlet4s",
    micrositePushSiteWith        := GitHub4s,
    name                         := "figlet4s-microsite",
    mdocExtraArguments           := Seq("--no-link-hygiene"),
    publish / skip               := true,
    mdocVariables := Map(
      "VERSION"       -> """\d+\.\d+\.\d""".r.findFirstIn(version.value).getOrElse(""),
      "SCALA_VERSION" -> """\d+\.\d+""".r.findFirstIn(scalaVersion.value).getOrElse(""),
    ),
  )

// Figlet4s Benchmarks project
lazy val figlet4sBenchmarks: Project = project
  .in(file("figlet4s-benchmarks"))
  .dependsOn(figlet4sCore)
  .settings(
    name                     := "figlet4s-benchmarks",
    description              := "Benchmarks for Figlet4s",
    publishArtifact          := false,
    logBuffered              := false,
    publish / skip           := true,
    Test / parallelExecution := false,
    Test / logBuffered       := false,
    resolvers               ++= SonatypeRepos,
    testFrameworks           += new TestFramework("org.scalameter.ScalaMeterFramework"),
    libraryDependencies ++= Seq(
      CatsCoreDep,
      ScalameterDep,
    ),
  )
