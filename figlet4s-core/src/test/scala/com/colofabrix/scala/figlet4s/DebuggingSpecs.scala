package com.colofabrix.scala.figlet4s

import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._

class DebuggingSpecs extends AnyFlatSpec with Matchers with OriginalFigletTesting with Figlet4sMatchers {

  "Debugging" should "help me fixing issues" taggedAs (SingleRunTest) in {
    val text = "n,'"

    val builder =
      Figlet4s
        .builder(text)
        .withInternalFont("3-d")
        .withHorizontalLayout(HorizontalLayout.ForceHorizontalSmushing)

    val computed = builder.render()
    val expected = renderWithFiglet(builder.options, text)

    computed.lines.head.value.foreach(println)
    computed should lookLike(expected)
  }

}
