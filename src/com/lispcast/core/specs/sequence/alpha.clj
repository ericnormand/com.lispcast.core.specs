(ns com.lispcast.core.specs.sequence.alpha
  (:require [clojure.spec.alpha :as s]))

(s/fdef clojure.core/first
        :args (s/cat :coll seqable?))

(s/fdef clojure.core/rest
        :args (s/cat :coll seqable?))

(s/fdef clojure.core/cons
        :args (s/cat :x any? :seq seqable?)
        :ret seq?)

(s/def :com.lispcast.core.specs.alpha/nthable
  (s/or :nil nil?
        :indexed #(instance? clojure.lang.Indexed %)
        :char-sequence #(instance? CharSequence %)
        :array #(-> % class .isArray)
        :random-access #(instance? java.util.RandomAccess %)
        :matcher #(instance? java.util.regex.Matcher %)
        :map-entry map-entry?
        :sequential sequential?))

(s/fdef clojure.core/distinct
        :args (s/or :transducer (s/cat)
                    :sequence (s/cat :coll :com.lispcast.core.specs.alpha/nthable))
        :ret (s/or :transducer ifn?
                   :sequence seqable?))



(s/fdef clojure.core/map
        :args (s/or :transducer (s/cat :f ifn?)
                    :sequence (s/cat :f ifn? :colls (s/+ seqable?)))
        :ret (s/or :transducer ifn?
                   :sequence seqable?)

        :fn (s/or :transducer #(and (:transdducer (:args %))
                                    (:transducer (:ret %)))
                  :sequence #(and (:sequence (:args %))
                                  (:sequence (:ret %)))))

(s/fdef clojure.core/filter
        :args (s/or :transducer (s/cat :f ifn?)
                    :sequence (s/cat :f ifn? :coll seqable?))
        :ret (s/or :transducer ifn?
                   :sequence seqable?))

