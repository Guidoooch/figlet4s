# Figlet4s

This is an implementation of [FIGlet](http://www.figlet.org/) in pure Scala with integrated fonts,
support for Cats and minimal dependencies.

This implementation follows the rules established in the [The FIGfont Version 2 FIGfont and
FIGdriver Standard](figfont_reference.txt).

## Installing dependencies

TODO

## Using impure functions

These examples show step-by-step how to use Figlet4s. In this scenario we don't want to use any particular effect,
and we want errors to be thrown as exceptions.

### Quick start

The general way to use Figlet4s involves 3 steps:

* first obtain a builder
* configure the options of the builder, if needed
* render a text into a FIGure

```scala
import com.colofabrix.scala.figlet4s.unsafe._

object QuickStartMain extends App {

  // Obtain an options builder
  val builder = Figlet4s.builder()

  // Render a text into a FIGure
  val figure = builder.render("Hello, World!")

  // Print the FIGure
  figure.print()
 
}
```

### Changing options

In this example we see some options that you can configure, and we see a more compact way of making the calls, without
storing objects at each step.
 

```scala
import com.colofabrix.scala.figlet4s.unsafe._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

object ShowcaseOptionsMain extends App {

  Figlet4s
    .builder("Hello, World!")                       // Create the options builder with a text to render
    .withMaxWidth(80)                               // Max width of the text
    .withInternalFont("alligator")                  // Set the font
    .defaultMaxWidth()                              // Go back to the default max  width
    .withHorizontalLayout(HorizontalFittingLayout)  // Choose a layout
    .text("Hello, Scala!")                          // Change the text to render
    .render()                                       // Render the text to a FIGure
    .print()                                        // Print the FIGURE

}
```

## Using cats' IO

To use cats' IO you import the package `com.colofabrix.scala.figlet4s.catsio` which provides objects, implicits and
enrichments to work with `IO`. All the functions and their arguments remain unchanged from the impure version, but the
return types are wrapped in the cats' `IO` monad.

### Quick start

This is the same example as in the impure version above, only it uses cats' `IO` and `IOApp`.

```scala
import cats.effect.IOApp
import com.colofabrix.scala.figlet4s.catsio._

object QuickStartIOMain extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      builder <- Figlet4s.builderF()              // Obtain an options builder
      figure  <- builder.render("Hello, World!")  // Render a text into a FIGure
      _       <- figure.print()                   // Print the FIGure
    } yield ExitCode.Success

}
```

### Changing options

This is the same example as above, only using IOApp. You can find a more comprehensive list of options in the example
above.

```scala
import cats.effect.IOApp
import com.colofabrix.scala.figlet4s.catsio._
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._

object ShowcaseOptionsIOMain extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      figure <- Figlet4s
                  .builder("Hello, World!")                      // Create the options builder with a text to render
                  .withMaxWidth(80)                              // Max width of the text
                  .withInternalFont("alligator")                 // Set the font
                  .defaultMaxWidth()                             // Go back to the default max width
                  .withHorizontalLayout(HorizontalFittingLayout) // Choose a layout
                  .text("Hello, Scala!")                         // Change the text to render
                  .render()                                      // Render the text to a FIGure
      _ <- figure.print()
    } yield ExitCode.Success

}
```

## Using the Figlet4s client

```scala
import com.colofabrix.scala.figlet4s.figfont.FIGfontParameters._
import com.colofabrix.scala.figlet4s.rendering.RenderOptions
import com.colofabrix.scala.figlet4s.unsafe._

object LowLevelMain extends App {

  // Load a font, choose the layout and max width
  val font     = Figlet4s.loadFontInternal("alligator")
  val layout   = HorizontalFittingLayout
  val maxWidth = 120
 
  // Build the render options
  val options = RenderOptions(font, layout, maxWidth)
 
  // Render a string
  Figlet4s.renderString("Hello, World!", options)
 
}
```

## List of options builder settings

* `text`: Use the specified Text Width.
* `defaultFont`: Use the default FIGfont 
* `withInternalFont`: Use the internal FIGfont with the specified fontName 
* `withFont`: Use the FIGfont with the specified fontPath 
* `withFont`: Use the specified FIGfont 
* `defaultHorizontalLayout`: Use the default Horizontal Layout 
* `withHorizontalLayout`: Use the specified Horizontal Layout 
* `defaultMaxWidth`: Use the default Max Width 
* `withMaxWidth`: Use the specified Max Width 

## Glossary of Figlet4s terms

Figlet4s defines several concepts that broadly correspond to the ones defined in the [The FIGfont Version 2 FIGfont and
FIGdriver Standard](figfont_reference.txt) but in this library they might assume a more specific meaning. 

**FIGfont**

A FIGlet Font is a map of characters to their FIG-representation, and the typographic settings used to display them.
 
**FIGcharacter**

It's a single FIGlet character, part of a FIGfont, that maps a single `Char` to its FIGlet representation and it's
composed of SubLines/SubColumns.
 
**FIGheader**

FIGLettering Font file header that contains thye raw configuration settings for the FIGfont.
 
**FIGure**

A FIGure is `String` rendered with a specific FIGfont ultimately built by concatenating and merging FIGcharacters
following a specific layout.
 
**SubLine and SubColumn**
 
Represents the SubLines/SubColumns in Figlet which are the String that compose each line/column of the FIGure or of a
FIGcharacter.

## Planned features

* Support for control files `*.flc`
* Support for zipped fonts
* Support for vertical layout
* Test and improve speed and memory performance

## License

MIT

## Author Information

[Fabrizio Colonna](mailto:colofabrix@tin.it)
