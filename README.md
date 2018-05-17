# com.lispcast.core.specs

A set of specs for Clojure core functions. These specs are meant to
advance the cause of complete specs for all Clojure's functions as
released by Clojure core team. These specs will be deprecated as soon
as clojure.core.specs comes out of Alpha.

However, right now, everything is in Alpha. Do not use this in
production, and certainly do not rely on these specs like they are
correct expressions of the design intent of Clojure. Creating specs
for things often involves changing the thing itself if the spec it
requires is complicated. I already have spoken with someone from the
core team who has confirmed that they may expand the domain of Clojure
core functions if it makes the specs simpler.

## Philosophy

Specs should define the intended domain and range of the functions
they are specing.

We follow the same contributor restrictions as clojure.core.specs so
that we can let them use this code. However, we use GitHub to manage
the code. All code merged to master must be written by someone with a
contributor agreement submitted to Cognitect.

We create property-based tests for good and bad inputs to functions to
cover all arities and major uses of Clojure core functions.

## License

Copyright © 2018 Eric Normand

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
