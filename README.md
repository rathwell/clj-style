# clj-style

clj-style is a css generation library for clojure.  It extends the gaka 
library by adding a few convenience macros and functions to provide a 
little bit different interface than gaka.

## Usage  (Under Construction)

[org.clojars.rathwell/clj-style "1.0.0"]

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
