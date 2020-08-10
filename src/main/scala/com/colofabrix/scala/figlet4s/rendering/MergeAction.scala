package com.colofabrix.scala.figlet4s.rendering

import cats._

/**
 * Result of the merge of data like SubColumns
 */
sealed trait MergeAction[+A]

object MergeAction {

  /** Represents a "keep the value and continue" condition */
  final case class Continue[A](value: A) extends MergeAction[A]
  /** Represents a "stop processing, keep use the value of the current iteration" condition */
  final case class CurrentLast[A](value: A) extends MergeAction[A]
  /** Represents a "stop processing, use value of last iteration" condition */
  final case object Stop extends MergeAction[Nothing]

  implicit val applicativeMergeAction: Applicative[MergeAction] = new Applicative[MergeAction] {
    def pure[A](x: A): MergeAction[A] =
      Continue(x)

    def ap[A, B](ff: MergeAction[A => B])(fa: MergeAction[A]): MergeAction[B] =
      (ff, fa) match {
        case (_, Stop)                            => Stop
        case (Stop, _)                            => Stop
        case (Continue(f), Continue(value))       => Continue(f(value))
        case (Continue(f), CurrentLast(value))    => CurrentLast(f(value))
        case (CurrentLast(f), Continue(value))    => CurrentLast(f(value))
        case (CurrentLast(f), CurrentLast(value)) => CurrentLast(f(value))
      }
  }
}
