// SBT Sources
Compile / scalaSource := baseDirectory.value / "src"

// SBT Scala version
scalaVersion := "2.12.11"

//  VERSIONS  //

lazy val AssemblyVersion        = "0.14.6"
lazy val BuildinfoVesion        = "0.9.0"
lazy val ErrorsSummaryVersion   = "0.6.3"
lazy val ExplicitDepsVersion    = "0.2.13"
lazy val NativePackagerVersion  = "1.3.25"
lazy val SbtStatsVersion        = "1.0.7"
lazy val ScalafmtVersion        = "2.3.0"
lazy val UpdatesVersion         = "0.5.0"
lazy val WartremoverVersion     = "2.4.7"

//  PLUGIN LIBRARIES  //

addSbtPlugin("com.eed3si9n"     % "sbt-assembly"              % AssemblyVersion)
addSbtPlugin("com.eed3si9n"     % "sbt-buildinfo"             % BuildinfoVesion)
addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % ExplicitDepsVersion)
addSbtPlugin("com.orrsella"     % "sbt-stats"                 % SbtStatsVersion)
addSbtPlugin("com.timushev.sbt" % "sbt-updates"               % UpdatesVersion)
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager"       % NativePackagerVersion)
addSbtPlugin("org.duhemm"       % "sbt-errors-summary"        % ErrorsSummaryVersion)
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"              % ScalafmtVersion)
addSbtPlugin("org.wartremover"  % "sbt-wartremover"           % WartremoverVersion)
