package com.colofabrix.scala.figlet4s

import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.testutils._
import com.colofabrix.scala.figlet4s.unsafe._
import org.scalatest.flatspec._
import org.scalatest.matchers.should._
import scala.concurrent.ExecutionContext
import scala.util._

class Figlet4sUnsafeSpecs extends AnyFlatSpec with Matchers with Figlet4sMatchers with OriginalFigletTesting {

  "Figlet4s" should "render a default text using the \"standard\" font" in {
    val computed = SpecsData.standardBuilder.render(SpecsData.standardInput)
    val expected = FIGure(
      SpecsData.standardBuilder.options.font,
      SpecsData.standardInput,
      Vector(SpecsData.standardLines.toSubcolumns),
    )
    computed should lookLike(expected)
  }

  it should "return the list of internal fonts containing at least the \"standard\" font" in {
    Figlet4s.internalFonts should contain(Figlet4sClient.defaultFont)
  }

  it should "load all internal fonts successfully" in {
    val loadingErrors = for {
      font  <- Figlet4s.internalFonts
      error <- interpretResult(font)(Try(Figlet4s.loadFontInternal(font)))
    } yield error

    loadingErrors shouldBe empty
  }

  it should "read a font from the file system" in {
    val cwd    = System.getProperty("user.dir")
    val font   = Try(Figlet4s.loadFont(s"$cwd/figlet4s-core/src/main/resources/fonts/standard.flf"))
    val result = interpretResult(Figlet4sClient.defaultFont)(Try(font))

    result shouldBe empty
  }

  it should "support loading of fonts in parallel" in {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    val data = Figlet4s
      .internalFonts
      .toList
      .take(10)
      .flatMap(Seq.fill(10)(_))

    val test = data.parTraverse { fontName =>
      IO(Figlet4s.loadFontInternal(fontName))
    }

    test.unsafeRunSync()
  }

  it should "render the texts as the original command line FIGlet does" taggedAs (SlowTest) in {
    figletRenderingTest { testData =>
      val testBuilder =
        defaultBuilder
          .text(testData.renderText)
          .withInternalFont(testData.fontName)
          .withHorizontalLayout(testData.horizontalLayout)
          .withPrintDirection(testData.printDirection)
          .withJustification(testData.justification)

      val computed = testBuilder.render()
      val expected = renderWithFiglet(testBuilder.options, testData.renderText)

      computed should lookLike(expected)
    }
  }

  it should "throw an exception when there is an error like a font that doesn't exist" in {
    assertThrows[FigletLoadingError] {
      Figlet4s
        .builder(SpecsData.standardInput)
        .withFont("non_existent")
        .render()
        .asString()
    }
  }

  "FIGure" should "return the same data for asSeq() and asString()" in {
    val figure     = SpecsData.standardBuilder.render(SpecsData.standardInput)
    val fromSeq    = figure.asSeq().mkString("\n")
    val fromString = figure.asString()
    fromSeq should equal(fromString)
  }

  it should "print the same data as asString()" in {
    val figure = SpecsData.standardBuilder.render(SpecsData.standardInput)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      figure.print()
    }

    val computed = stream.toString()
    val expected = figure.asString() + "\n"

    computed should equal(expected)
  }

  //  Support  //

  private def interpretResult(font: String): PartialFunction[Try[_], Option[String]] = {
    case Failure(fe @ FigletException(message)) =>
      Some(s"${fe.getClass.getSimpleName} on $font: $message")
    case Failure(exception: Throwable) =>
      Some(s"Exception on $font: ${exception.getMessage}")
    case Success(_) =>
      None
  }

  private val defaultBuilder =
    Figlet4s
      .builder()
      .withInternalFont(Figlet4sClient.defaultFont)
      .withHorizontalLayout(HorizontalLayout.FullWidth)

}
