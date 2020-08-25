package com.colofabrix.scala.figlet4s.options

import cats.effect.Sync
import cats.implicits._
import com.colofabrix.scala.figlet4s.Figlet4sClient
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options.BuilderAction._
import com.colofabrix.scala.figlet4s.options.OptionsBuilder._

/**
 * Builder of rendering options
 */
final class OptionsBuilder(private val actions: List[BuilderAction] = List.empty) {

  /** Use the specified Text Width */
  def text(text: String): OptionsBuilder =
    addAction(SetTextAction(text))

  //  Font  //

  /** Use the default FIGfont */
  def defaultFont(): OptionsBuilder =
    addAction(DefaultFontAction)

  /** Use the internal FIGfont with the specified fontName */
  def withInternalFont(fontName: String): OptionsBuilder =
    addAction(LoadInternalFontAction(fontName))

  /** Use the FIGfont with the specified fontPath */
  def withFont(fontPath: String, encoding: String = "ISO8859_1"): OptionsBuilder =
    addAction(LoadFontAction(fontPath, encoding))

  /** Use the specified FIGfont */
  def withFont(font: FIGfont): OptionsBuilder =
    addAction(SetFontAction(font))

  //  Horizontal Layout  //

  /** Use the default Horizontal Layout */
  def defaultHorizontalLayout(): OptionsBuilder =
    addAction(DefaultHorizontalLayout)

  /** Use the specified Horizontal Layout */
  def withHorizontalLayout(layout: HorizontalLayout): OptionsBuilder =
    addAction(SetHorizontalLayout(layout))

  //  Max Width  //

  /** Use the default Max Width */
  def defaultMaxWidth(): OptionsBuilder =
    addAction(DefaultMaxWidthAction)

  /** Use the specified Max Width */
  def withMaxWidth(maxWidth: Int): OptionsBuilder =
    addAction(SetMaxWidthAction(maxWidth))

  //  Print Direction  //

  /** Use the default Print Direction */
  def defaultPrintDirection(): OptionsBuilder =
    addAction(DefaultPrintDirection)

  /** Use the specified Print Direction */
  def withPrintDirection(direction: PrintDirection): OptionsBuilder =
    addAction(SetPrintDirection(direction))

  //  Justification  //

  /** Use the default Justification */
  def defaultJustification(): OptionsBuilder =
    addAction(DefaultJustification)

  /** Use the specified Justification */
  def withJustification(justification: Justification): OptionsBuilder =
    addAction(SetJustification(justification))

  //  Support  //

  private[figlet4s] def compile[F[_]: Sync]: F[BuildData] =
    OptionsBuilder.compile[F](this)

  private def addAction(action: BuilderAction): OptionsBuilder =
    new OptionsBuilder(action :: this.actions.filter(!BuilderAction.sameActionAs(action)(_)))

}

private[figlet4s] object OptionsBuilder {

  final case class BuildData(
      font: Option[FigletResult[FIGfont]] = None,
      horizontalLayout: HorizontalLayout = HorizontalLayout.FontDefault,
      justification: Justification = Justification.FontDefault,
      maxWidth: Option[Int] = None,
      printDirection: PrintDirection = PrintDirection.FontDefault,
      text: String = "",
  )

  private type ActionCompiler[F[_]] = PartialFunction[(BuildData, BuilderAction), F[BuildData]]

  /**
   * Compiler to run BuilderAction that create BuildData, generic in the effect
   */
  def compile[F[_]: Sync](self: OptionsBuilder): F[BuildData] =
    self
      .actions
      .foldM(BuildData())(aggregate(allCompilers))

  private def allCompilers[F[_]: Sync]: List[ActionCompiler[F]] =
    List(
      compileFonts[F],
      compileHorizontalLayout[F],
      compileJustification[F],
      compileMaxWidth[F],
      compilePrintDirection[F],
      compileText[F],
    )

  /** Compiles the settings for Text */
  private def compileText[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, SetTextAction(text)) =>
      Sync[F].pure(buildData.copy(text = text))
  }

  /** Compiles the settings for Fonts */
  private def compileFonts[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, DefaultFontAction) =>
      Figlet4sClient
        .loadFontInternal[F]("standard")
        .map { font =>
          buildData.copy(font = Some(font))
        }

    case (buildData, SetFontAction(font)) =>
      Sync[F].pure(buildData.copy(font = Some(font.validNec)))

    case (buildData, LoadFontAction(fontPath, encoding)) =>
      Figlet4sClient
        .loadFont[F](fontPath, encoding)
        .map { font =>
          buildData.copy(font = Some(font))
        }

    case (buildData, LoadInternalFontAction(fontName)) =>
      Figlet4sClient
        .loadFontInternal[F](fontName)
        .map { font =>
          buildData.copy(font = Some(font))
        }
  }

  /** Compiles the settings for Max Width */
  private def compileMaxWidth[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, DefaultMaxWidthAction) =>
      Sync[F].pure(buildData.copy(maxWidth = None))

    case (buildData, SetMaxWidthAction(maxWidth)) =>
      Sync[F].pure(buildData.copy(maxWidth = Some(maxWidth)))
  }

  /** Compiles the settings for Horizontal Layout */
  private def compileHorizontalLayout[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, DefaultHorizontalLayout) =>
      Sync[F].pure(buildData.copy(horizontalLayout = HorizontalLayout.FontDefault))

    case (buildData, SetHorizontalLayout(layout)) =>
      Sync[F].pure(buildData.copy(horizontalLayout = layout))
  }

  /** Compiles the settings for Print Direction */
  private def compilePrintDirection[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, DefaultPrintDirection) =>
      Sync[F].pure(buildData.copy(printDirection = PrintDirection.FontDefault))

    case (buildData, SetPrintDirection(direction)) =>
      Sync[F].pure(buildData.copy(printDirection = direction))
  }

  /** Compiles the settings for Justification */
  private def compileJustification[F[_]: Sync]: ActionCompiler[F] = {
    case (buildData, DefaultJustification) =>
      Sync[F].pure(buildData.copy(printDirection = PrintDirection.FontDefault))

    case (buildData, SetJustification(justification)) =>
      Sync[F].pure(buildData.copy(justification = justification))
  }

  @SuppressWarnings(Array("org.wartremover.warts.TraversableOps"))
  private def aggregate[F[_]: Sync](data: List[ActionCompiler[F]]): (BuildData, BuilderAction) => F[BuildData] =
    Function.untupled(data.reduce(_ orElse _))

}
