package com.colofabrix.scala.figlet4s.api

import cats._
import cats.effect._
import cats.implicits._
import com.colofabrix.scala.figlet4s.errors._
import com.colofabrix.scala.figlet4s.figfont._
import com.colofabrix.scala.figlet4s.options._
import com.colofabrix.scala.figlet4s.rendering._
import java.io.File
import scala.collection.Iterator
import scala.io._

/**
 * Layer of API internal to figlet4s, used to have uniform and generic access to resources when implementing client APIs
 */
private[figlet4s] object InternalAPI {

  /**
   * The list of available internal fonts
   */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def internalFonts[F[_]: Sync]: F[Vector[String]] = {
    val jar = getClass.getProtectionDomain.getCodeSource.getLocation

    // Opening the JAR to look at resources
    withResource(new java.util.zip.ZipInputStream(jar.openStream)) { zip =>
      Sync[F].delay {
        Iterator
          .continually(zip.getNextEntry)
          .takeWhile(_ != null)
          .map(zipEntry => new File(zipEntry.getName))
          .filter(path => path.getPath.startsWith("fonts") && path.getName.endsWith(".flf"))
          .map(_.getName.replace(".flf", ""))
          .toVector
      }
    }
  }

  /**
   * Renders a given text as a FIGure
   */
  def renderString[F[_]: Sync](text: String, options: RenderOptions): F[FIGure] =
    Sync[F].pure {
      HorizontalTextRenderer.render(text, options)
    }

  /**
   * Loads one of the internal FIGfont
   */
  def loadFontInternal[F[_]: Sync](name: String = "standard"): F[FigletResult[FIGfont]] =
    for {
      path    <- Applicative[F].pure(s"fonts/$name.flf")
      decoder <- fileDecoder[F]("ISO8859_1")
      font    <- withResource(Source.fromResource(path)(decoder))(interpretFile[F](path))
    } yield font

  /**
   * Loads a FIGfont from file
   */
  def loadFont[F[_]: Sync](path: String, encoding: String): F[FigletResult[FIGfont]] =
    for {
      decoder <- fileDecoder[F](encoding)
      font    <- withResource(Source.fromResource(path)(decoder))(interpretFile[F](path))
    } yield font

  //  Support  //

  private def fileDecoder[F[_]: Applicative](encoding: String): F[Codec] =
    Applicative[F].pure {
      Codec(encoding)
        .decoder
        .onMalformedInput(java.nio.charset.CodingErrorAction.REPORT)
    }

  private def interpretFile[F[_]: Applicative](path: String)(source: BufferedSource): F[FigletResult[FIGfont]] =
    Applicative[F].pure {
      val name  = new File(path).getName.split('.').init.mkString("")
      val lines = source.getLines()
      FIGfont(name, lines)
    }

  // See: https://medium.com/@dkomanov/scala-try-with-resources-735baad0fd7d
  @SuppressWarnings(Array("org.wartremover.warts.All"))
  private def withResource[F[_]: MonadThrowable, R <: AutoCloseable, A](resource: => R)(f: R => F[A]): F[A] = {
    var exception: Throwable = null
    try {
      f(resource)
    } catch {
      case e: Throwable =>
        exception = e
        MonadThrowable[F].raiseError[A](FigletLoadingError(e.getMessage, e))
    } finally {
      if (exception != null) {
        try {
          resource.close()
        } catch {
          case suppressed: Throwable =>
            exception.addSuppressed(suppressed)
        }
      } else {
        resource.close()
      }
    }
  }

}
