package com.colofabrix.scala.figlet4s.figfont.header

import enumeratum.values._
import scala.collection.BitSet

/**
 * Old layout parameter
 */
sealed abstract class OldLayout(val value: Int, val name: String) extends IntEnumEntry

object OldLayout extends IntEnum[OldLayout] {
  /** Full-width layout by default */
  final case object FullWidthLayoutOldlayout extends OldLayout(value = -1, name = "FullWidthLayout")
  /** Horizontal fitting (kerning) layout by default */
  final case object KerningLayoutOldlayout extends OldLayout(value = 0, name = "KerningLayout")
  /** Apply horizontal smushing rule 1 by default */
  final case object HorizontalSmushingRule1Oldlayout extends OldLayout(value = 1, name = "HorizontalSmushingRule1")
  /** Apply horizontal smushing rule 2 by default */
  final case object HorizontalSmushingRule2Oldlayout extends OldLayout(value = 2, name = "HorizontalSmushingRule2")
  /** Apply horizontal smushing rule 3 by default */
  final case object HorizontalSmushingRule3Oldlayout extends OldLayout(value = 4, name = "HorizontalSmushingRule3")
  /** Apply horizontal smushing rule 4 by default */
  final case object HorizontalSmushingRule4Oldlayout extends OldLayout(value = 8, name = "HorizontalSmushingRule4")
  /** Apply horizontal smushing rule 5 by default */
  final case object HorizontalSmushingRule5Oldlayout extends OldLayout(value = 16, name = "HorizontalSmushingRule5")
  /** Apply horizontal smushing rule 6 by default */
  final case object HorizontalSmushingRule6Oldlayout extends OldLayout(value = 32, name = "HorizontalSmushingRule6")

  def apply(value: Int): Vector[OldLayout] =
    if (value == -1) {
      Vector(FullWidthLayoutOldlayout)

    } else {
      val bitSet = value
        .toBinaryString
        .reverse
        .zipWithIndex
        .flatMap {
          case ('0', _) => IndexedSeq()
          case ('1', n) => IndexedSeq(n)
          case (_, _)   => IndexedSeq() // Never gets here thanks to .toBinaryString
        }

      BitSet
        .fromSpecific(bitSet)
        .toVector
        .map(findValues)
    }

  val values = findValues
}
