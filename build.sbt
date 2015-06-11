name := """cheboleSer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
   "cglib"  % "cglib-nodep"  % "2.1_3",
   "org.mindrot" % "jbcrypt" % "0.3m",
   "javax.inject" % "javax.inject" % "1"
)
