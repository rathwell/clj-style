# gaka-plus

gaka-plus is an extension of the gaka library that adds a few convenience macros and
functions to provide a slightly different interface for building css.

## Usage

[org.clojars.rathwell/gaka-plus "1.0.0"]

overview
  - should not interfere with gaka


defining rules and mixins
  - rules
    - groups
    - same format as gaka (see docs)
  - mixins


nesting
  - mixins in mixins
  - mixins in rules
  - rules in rules
    - set group to :ignore (or any other value that is not used by main rules)


render
  - :default group
  - specific group
  - rule
  - mixin
    - use for inline css

save
  - :default group
  - specific group

remove indentation in render and save
  - (binding [gaka.core/*print-indent* false] (save))


## License

Copyright (C) 2011 Mark Rathwell

Distributed under the Eclipse Public License, the same as Clojure.
