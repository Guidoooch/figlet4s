// package com.colofabrix.scala.figlet4s

// import com.colofabrix.scala.figlet4s.options._
// import com.colofabrix.scala.figlet4s.testutils._
// import com.colofabrix.scala.figlet4s.unsafe._
// import org.scalatest.flatspec._
// import org.scalatest.matchers.should._

// class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

//   "Debugging" should "help me fixing issues" taggedAs (ManualRunTest) in {
//     val text = "Wrl"

//     val builder =
//       Figlet4s
//         .builder(text)
//         .withMaxWidth(120)
//         .withInternalFont("standard")
//         .withHorizontalLayout(HorizontalLayout.HorizontalSmushing)
//         // .withJustification(Justification.FlushLeft)
//         .withPrintDirection(PrintDirection.RightToLeft)

//     println("COMPUTED:")
//     val computed = builder.render()
//     computed.foreachLine(line => println("|" + line + "|"))

//     println("EXPECTED:")
//     val expected = renderWithFiglet(builder.options, text)
//     expected.foreachLine(line => println("|" + line + "|"))

//     // computed should lookLike(expected)
//   }

// }
