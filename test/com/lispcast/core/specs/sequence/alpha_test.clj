(ns com.lispcast.core.specs.sequence.alpha-test
  (:require [com.lispcast.core.specs.sequence.alpha :as sut]
            [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [expound.alpha :as expound]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]))

(set! s/*explain-out* (expound/custom-printer {:print-specs? false :show-valid-values? true}))
(stest/instrument)

(def gen-fun
  (gen/let [v gen/simple-type]
    (constantly v)))

(def gen-array
  (gen/let [v (gen/vector gen/any)]
    (to-array v)))

(def gen-seqable
  (gen/one-of [gen/string
               gen-array
               (gen/return nil)
               gen/any]))

(def gen-nthable
  (gen/one-of [gen/string
               gen-array
               (gen/return nil)
               (gen/vector gen/simple-type)
               (gen/list gen/simple-type)]))

(def gen-non-nthable
  (gen/such-that #(not (s/valid? :com.lispcast.core.specs.alpha/nthable %))
                 (gen/one-of [(gen/map gen/simple-type gen/simple-type)
                              gen/simple-type])))

(def gen-non-seqable
  (gen/such-that (complement seqable?) gen/simple-type))

(def gen-non-fn
  (gen/such-that (complement ifn?) gen/simple-type))

(defmacro succeeds [& body]
  `(try
     (let [v# (do ~@body)]
       (when (seq? v#)
         (dorun v#)))
     true
     (catch Throwable t#
       false)))

(defmacro fails [& body]
  `(try
     (let [v# (do ~@body)]
       (when (seq? v#)
         (dorun v#)))
     false
     (catch clojure.lang.ExceptionInfo ei#
       true)
     (catch Throwable t#
       false)))

;; test to make sure we can call it without error
(defspec first-good 10
  (prop/for-all [s gen-seqable]
    (succeeds (first s))))

(defspec first-bad 10
  (prop/for-all [s gen-non-seqable]
    (fails (first s))))


(defspec rest-good 10
  (prop/for-all [s gen-seqable]
    (succeeds (rest s))))

(defspec rest-bad 10
  (prop/for-all [s gen-non-seqable]
    (fails (rest s))))


(defspec cons-good 10
  (prop/for-all [a gen/simple-type
                 s gen-seqable]
    (succeeds (cons 1 s))))

(defspec cons-bad 10
  (prop/for-all [a gen/simple-type
                 s gen-non-seqable]
    (fails (cons a s))))

(defspec distinct-good 10
  (prop/for-all [a gen-nthable]
    (succeeds (distinct a))))

(defspec distinct-transducer-good 10
  (prop/for-all []
    (succeeds (distinct))))

(defspec distinct-bad 10
  (prop/for-all [a gen-non-nthable]
    (fails (distinct a))))

(defspec map-good 10
  (prop/for-all [f gen-fun
                 s (gen/such-that seq (gen/vector gen-seqable))]
    (succeeds (apply map f s))))

(defspec map-bad 10
  (prop/for-all [f gen-fun
                 s (gen/such-that seq (gen/vector gen-non-seqable))]
    (fails (apply map f s))))

(defspec map-bad-fn 10
  (prop/for-all [nf gen-non-fn
                 s (gen/such-that seq (gen/vector gen-seqable))]
    (fails (apply map nf s))))

(defspec map-transducer-good 10
  (prop/for-all [f gen-fun]
    (succeeds (map f))))

(defspec map-transducer-bad 10
  (prop/for-all [nf gen-non-fn]
    (fails (map nf))))


(defspec filter-good 10
  (prop/for-all [a gen-seqable]
    (succeeds (filter identity a))))

(defspec filter-bad 10
  (prop/for-all [a gen-non-seqable]
    (fails (filter identity a))))

(defspec filter-bad-fn 10
  (prop/for-all [a gen-seqable
                 nf gen-non-fn]
    (fails (filter nf a))))

(defspec filter-transducer-good 10
  (prop/for-all []
    (succeeds (filter identity))))

(defspec filter-transducer-bad 10
  (prop/for-all [nf gen-non-fn]
    (fails (filter nf))))



(deftest check-some-functions
  (is (thrown? clojure.lang.ExceptionInfo (doall (map 1 [2]))))
  (is (thrown? clojure.lang.ExceptionInfo (doall (map :1 3))))
  (is (thrown? clojure.lang.ExceptionInfo (doall (map 1))))

  (is (doall (map even? nil)))
  (is (doall (map identity "")))
  (is (doall (map identity (to-array [1 2 3]))))

  (is (thrown? clojure.lang.ExceptionInfo (doall (filter 1 [2]))))
  (is (thrown? clojure.lang.ExceptionInfo (doall (filter even? 2))))
  (is (thrown? clojure.lang.ExceptionInfo (doall (filter 3)))))

