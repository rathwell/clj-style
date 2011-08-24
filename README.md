# clj-style 1.0.0

A CSS generation library for Clojure.  It extends the 
[gaka](https://github.com/briancarper/gaka) library with a 
few convenience macros and functions to provide a little bit 
different interface.  While there should be no issues using
pieces of both `clj-style` and `gaka`, you would generally
only use one.

## Usage

`clj-style` is a fairly simple library.  Usage essentially consists
of adding the reference to your project, defining some rules and
mixins, and creating files.

### Installation

Add the entry to your project.clj: 

```clojure
[org.clojars.rathwell/clj-style "1.0.0"]
```

### Rules

Rules are defined with the `defrule` macro, which takes a name for the rule
and a vector representing a rule, in the format of `gaka` (see
[the gaka docs](https://github.com/briancarper/gaka) for a complete 
introduction to the format).

```clojure
(require '[clj-style.core :as cs])

(cs/defrule  div-foo
  [:div#foo
   :margin "0px"
   [:span.bar
    :color "black"
    :font-weight "bold"
    [:a:hover
     :text-decoration "none"]]])
;=> #'user/div-foo

div-foo
;=> [:div#foo :margin "0px" [:span.bar :color "black" :font-weight "bold" [:a:hover :text-decoration "none"]]]
```

You can also specify a group (as a keyword) that the rule should belong 
to.  Groups are described in more detail later, but for now they basically 
serve to organize your rules for output to separate files.  If you do not
specify a group, then :default will be used.

```clojure
(cs/defrule div-baz :screen
  [:div#baz
   :padding :5px
   :margin :10px])
```

The `render` function will render and return the rule as a formatted css string.

```clojure
(cs/render div-baz)
;=> "div#baz {\n  padding: 5px;\n  margin: 10px;}\n\n"

(println (cs/render div-baz))
;=> div#baz {
;=>   padding: 5px;
;=>   margin: 10px;}
```

### Mixins

You can easily define mixins with the `defmixin` macro.  This macro expects
a name for the mixin, and an arbitrary number of property/value pairs.

```clojure
(require '[clj-style.core :as cs])

(cs/defmixin blue
  :color :blue)
;=> #'user/blue

blue
;=> (:color :blue)

(cs/defmixin p-foo
  :margin :0px
  :width "80%")
;=> #'user/blue

p-foo
;=> (:margin :0px :width "80%")
```

As you can see, this is just syntactic sugar for defining a list of properties.
These mixins can then be mixed into rules or other mixins.

```clojure
(cs/defmixin blue-on-black
  blue
  :background-color :black)
;=> #'user/blue-on-black

blue-on-black
;=> (:color :blue :background-color :black)

(cs/defrule blue-div
  [:div
   :width :100px
   blue-on-black])
;=> #'user/blue-div

blue-div
;=> [:div :width :100px (:color :blue :background-color :black)]
```

The `render` function can be used with mixins to generate css suitable
for inline style declarations.

```clojure
(cs/render blue)
;=> "color: blue;"

(cs/render blue-on-black)
;=> "color: blue; background-color: black;"

(str "<p style=\"" (cs/render blue) "\">hello world</p>")
;=> "<p style=\"color: blue;\">hello world</p>"
```

### Groups

Groups are the way `clj-style` organizes rules for rendering and saving.
It is not a perfect system, by any means, but it gives you a couple of
simple options for creating multiple .css files.

When defining a rule, as described above, you have the option to specify a 
group that the rule will belong to.  If you do not specify one, then :default
is used.  If you only plan to output one css file, then there is no reason
to worry about groups.

When the `render` function is called with no arguments, or when the `save` 
function is called with only a filename, those functions will render, or
save respectively, all of the rules in the :default group.  By specifying 
groups when defining rules, you can then selectively save or render only
the rules from a specific group.

```clojure
(require '[clj-style.core :as cs])

(cs/defrule div-foo :main
  [:div#foo
   :padding :5px
   :margin :10px])
;=> #'user/div-foo

(cs/defrule div-bar :main
  [:div#bar
   :padding :10px
   :margin :20px])
;=> #'user/div-bar

(cs/defrule div-baz :extra
  [:div#baz
   :padding :20px
   :margin :30px])
;=> #'user/div-baz

(cs/render :main)
;=> "div#foo {\n  padding: 5px;\n  margin: 10px;}\n\ndiv#bar {\n  padding: 10px;\n  margin: 20px;}\n\n"

(cs/render :extra)
;=> "div#baz {\n  padding: 20px;\n  margin: 30px;}\n\n"

(cs/save "styles-main.css" :main)

(cs/save "styles-extra.css" :extra)
```

### Nesting

The `gaka` format allows nesting of selectors inherently.  This should generally fulfill
your nesting needs.  `clj-stye` does also allow you to nest a defined rule in another 
rule definition, however, there is something you need to consider when doing this.

If you define a rule, without specifying a group, it will be tracked in the :default
group.  If you then define another rule, which contains the first rule, that whole rule
will be tracked in the :default group.  Now, when you output the css with `save` or `render`
you have a duplicate rule being tracked that you probably don't want.

To address this, for the rules that are only intended to be nested, it is probably best 
to define them with an unused group, for example :ignore.

```clojure
(require '[clj-style.core :as cs])

(cs/defrule link :ignore [:a :text-decoration :none])
;=> #'user/link

(cs/defrule widget [:.widget :color :black link])
;=> #'user/widget

(cs/render)
;=> ".widget {\n  color: black;}\n\n  .widget a {\n    text-decoration: none;}\n\n"
```

Now only the full rule (widget) is in the output, which is probably what we want.


### Reset

There are times, for example when playing around at a repl, or possibly for file
output reasons, that you will want or need to clear out the atom that tracks
groups and rules.  For this reason, there is a function called `reset-rules!`.
This does not affect the vars bound to your defined rules, it only clears
the tracking of the rules and groups when you may need it.

```clojure
(cs/reset-rules!)
```

### Output a File

As previously mentioned, the `save` function will output the css from a group
to the specified file.  Generally, you will define all of your rules, then
throw a call to save at the top level so an output file is created each time
the code is required.

```clojure
(require '[clj-style.core :as cs])

(cs/defrule div-foo
  [:div#foo
   :padding :5px
   :margin :10px])

(cs/defrule div-bar
  [:div#bar
   :padding :10px
   :margin :20px])

(cs/defrule div-baz 
  [:div#baz
   :padding :20px
   :margin :30px])

(cs/save "styles.css")
```
 
### Multiple Files

When you intend to output multiple files, there are a couple of ways to ga about it.

1. Specify a group for every rule definition and save each group to a different file.

    ```clojure
    (require '[clj-style.core :as cs])

    (cs/defrule div-foo :screen
      [:div#foo
       :padding :5px
       :margin :10px])

    (cs/defrule div-bar :screen
      [:div#bar
       :padding :10px
       :margin :20px])

    (cs/defrule div-foo-p :print
      [:div#foo
       :padding :20px
       :margin :30px])

    (cs/defrule div-bar-p :print
      [:div#bar
       :padding :20px
       :margin :30px])

    (cs/save "browser-styles.css" :screen)

    (cs/save "printer-styles.css" :print)
    ```

2. Don't worry about specifying groups, just use the default.  Instead, reset the 
rule tracking before defining the rules for each file.  Most likely, one clojure
file would correspond to one css output file, and you would reset at the start of
each file and save at the end of each.

    ```clojure
    (ns css.screen
      (:require [clj-style.core :as cs]))

    (cs/reset-rules!)

    (cs/defrule div-foo
      [:div#foo
       :padding :5px
       :margin :10px])

    (cs/defrule div-bar
      [:div#bar
       :padding :10px
       :margin :20px])

    (cs/save "browser-styles.css")

    ;;;;;;;;;;;

    (ns css.print
      (:require [clj-style.core :as cs]))

    (cs/reset-rules!)

    (cs/defrule div-foo
      [:div#foo
       :padding :20px
       :margin :30px])

    (cs/defrule div-bar
      [:div#bar
       :padding :20px
       :margin :30px])

    (cs/save "printer-styles.css")
    ```

### Indentation / Minification

`clj-style` does not offer complete whitespace removal, or any kind of
minification.  The only offering is to take advantage of `gaka`'s ability
to turn off the indenting feature, removing a little whitespace.  You may
want to create your own whitespace removal and minification as a part of your 
build process.

Example of turning off the automatic indenting:

```clojure
(binding [gaka.core/*print-indent* false] 
  (cs/save "style.css"))
```

## License

Copyright (C) 2011 Mark Rathwell

Distributed under the Eclipse Public License, the same as Clojure.
