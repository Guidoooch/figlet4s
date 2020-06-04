package com.colofabrix.scala.figlet4s.figfont

import cats.data.Validated._
import cats.implicits._
import com.colofabrix.scala.figlet4s._
import com.colofabrix.scala.figlet4s.figfont.FIGheaderParameters._

/**
 * FIGlet file header that contains raw configuration settings for the FIGfont
 */
final case class FIGheader private[figlet4s] (
    signature: String,
    hardblank: String,
    height: Int,
    baseline: Int,
    maxLength: Int,
    oldLayout: Vector[OldLayout],
    commentLines: Int,
    printDirection: Option[PrintDirection],
    fullLayout: Option[Vector[FullLayout]],
    codetagCount: Option[Int],
)

final object FIGheader {
  private val SIGNATURE_INDEX: Int      = 0
  private val HEIGHT_INDEX: Int         = 1
  private val BASELINE_INDEX: Int       = 2
  private val MAXLENGTH_INDEX: Int      = 3
  private val OLDLAYOUT_INDEX: Int      = 4
  private val COMMENTLINES_INDEX: Int   = 5
  private val PRINTDIRECTION_INDEX: Int = 6
  private val FULLLAYOUT_INDEX: Int     = 7
  private val CODETAGCOUNT_INDEX: Int   = 8

  /**
   * Creates a new FLF Header from a the string representing the header
   */
  def apply(line: String): FigletResult[FIGheader] = {
    val splitLine = line.split(" ").toVector

    if (splitLine.length < 6 || splitLine.length > 9)
      FIGheaderError(s"Wrong number of parameters in FLF header. Found ${splitLine.length.toString} parameters").invalidNec
    else {
      val (signatureText, hardblankText) = splitLine(SIGNATURE_INDEX).splitAt(5)

      val signatureV      = validateSignature(signatureText)
      val hardblankV      = validateHardblank(hardblankText)
      val heightV         = validateHeight(splitLine(HEIGHT_INDEX))
      val baselineV       = validateBaseline(splitLine(BASELINE_INDEX))
      val maxLengthV      = validateMaxLength(splitLine(MAXLENGTH_INDEX))
      val oldLayoutV      = validateOldLayout(splitLine(OLDLAYOUT_INDEX))
      val commentLinesV   = validateCommentLines(splitLine(COMMENTLINES_INDEX))
      val printDirectionV = validatePrintDirection(splitLine.get(PRINTDIRECTION_INDEX.toLong))
      val fullLayoutV     = validateFullLayout(splitLine.get(FULLLAYOUT_INDEX.toLong))
      val codetagCountV   = validateCodetagCount(splitLine.get(CODETAGCOUNT_INDEX.toLong))

      // format: off
      (signatureV, hardblankV, heightV, baselineV, maxLengthV, oldLayoutV, commentLinesV, printDirectionV, fullLayoutV, codetagCountV)
        .mapN(FIGheader.apply)
      // format: on
    }
  }

  private def validateSignature(signature: String): FigletResult[String] =
    Option
      .when(signature == "flf2a")(signature)
      .toValidNec(FIGheaderError(s"Wrong FLF signature: $signature"))

  private def validateHardblank(hardblank: String): FigletResult[String] =
    Option
      .when(hardblank.length == 1)(hardblank)
      .toValidNec(FIGheaderError(s"The hardblank '$hardblank' is not composed of only one character"))

  private def validateHeight(height: String): FigletResult[Int] =
    height
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'height': $height"))

  private def validateBaseline(baseline: String): FigletResult[Int] =
    baseline
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'baseline': $baseline"))

  private def validateMaxLength(maxlength: String): FigletResult[Int] =
    maxlength
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'maxLength': $maxlength"))

  private def validateOldLayout(oldLayout: String): FigletResult[Vector[OldLayout]] =
    oldLayout
      .toIntOption
      .map(OldLayout(_))
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'oldLayout': $oldLayout"))

  private def validateCommentLines(commentLines: String): FigletResult[Int] =
    commentLines
      .toIntOption
      .toValidNec(FIGheaderError(s"Couldn't parse header field 'commentLines': $commentLines"))

  private def validatePrintDirection(printDirection: Option[String]): FigletResult[Option[PrintDirection]] =
    printDirection.traverse { value =>
      value
        .toIntOption
        .map(PrintDirection(_))
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'printDirection': $printDirection"))
    }

  private def validateFullLayout(fullLayout: Option[String]): FigletResult[Option[Vector[FullLayout]]] =
    fullLayout.traverse { value =>
      value
        .toIntOption
        .map(FullLayout(_))
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'fullLayout': $fullLayout"))
    }

  private def validateCodetagCount(codetagCount: Option[String]): FigletResult[Option[Int]] =
    codetagCount.traverse { value =>
      value
        .toIntOption
        .toValidNec(FIGheaderError(s"Couldn't parse header field 'codetagCount': $codetagCount"))
    }
}
