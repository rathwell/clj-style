# clj-style 1.0.0

A CSS generation library for Clojure.  It extends the 
[`gaka`](https://github.com/briancarper/gaka) library with a 
few convenience macros and functions to provide a little bit 
different interface.  While there should be no issues using
elements from both `clj-style` and `gaka`, you will generally
only use one.

## Usage

`clj-style` is a fairly simple library.  Usage essentially consists
of adding the reference to your project, defining some rules and
mixins, and creating files.

### Installation

[org.clojars.rathwell/clj-style "1.0.0"]

### Rules

Rules are defined with the `defrule` macro, which takes a name for the rule
and a vector representing a rule, in the format of `gaka` (see
[the gaka docs](https://github.com/briancarper/gaka) for a complete 
introduction to the format.


You can also specify a group (as a keyword) that the rule should belong 
to.  Groups are described in more detail later, but for now they basically 
serve to organize your rules for output to separate files.


### Mixins

You can easily define mixins with the `defmixin` macro.  This macro expects
a name for the mixin, and an arbitrary number of property/value pairs.


As you can see, this is just syntactic for defining a list of properties.
These mixins can then be mixed into rules or other mixins.


The `render` function can be used with mixins to generate css suitable
for inline style declarations.


### Nesting

  - rules in rules
    - set group to :ignore (or any other value that is not used by main rules)

### Saving Files

 -:default group
 - specific group

### Groups

Groups are the way clj-style organizes rules for rendering and saving.
It is not a perfect system, by any means, but it gives you a couple of
simple options for creating multiple .css files.

render
  - :default group
  - specific group

### Notes

remove indentation in render and save
  - (binding [gaka.core/*print-indent* false] (save))


## License

Copyright (C) 2011 Mark Rathwell

Distributed under the Eclipse Public License, the same as Clojure.
