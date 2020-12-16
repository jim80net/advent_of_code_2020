(ns d16.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [d16.core :as c]))

(def parse-input-test-result (try (with-open [r (io/reader "input.test.result.edn")]
                                    (clojure.edn/read (java.io.PushbackReader. r)))
                                  (catch java.io.IOException e
                                    (.println *err* (format "Couldn't open '%s': %s%n" "input.test.result.edn" (.getMessage e))))
                                  (catch RuntimeException e
                                    (.println *err* (format "Error parsing edn file '%s': %s%n" "input.test.result.edn" (.getMessage e))))))

(deftest parse-input-test
  (try (with-open [r (io/reader "input.test")]
         (is (= parse-input-test-result (c/parse-input (line-seq r)))))
       (catch java.io.IOException e
         (.println *err* (format "ERROR: Couldn't open '%s': %s%n" "input.test" (.getMessage e))))))

(def rules
  (apply concat (vals (get parse-input-test-result "rules"))))

(deftest valid-field?-test
  (is (true? (c/valid-field? rules 7)))
  (is (true? (c/valid-field? rules 3)))
  (is (true? (c/valid-field? rules 47)))
  (is (false? (c/valid-field? rules 4)))
  (is (false? (c/valid-field? rules 55)))
  (is (false? (c/valid-field? rules 12))))

(deftest part-1-test
  (is (= 11172 (c/part-1 parse-input-test-result))))

(deftest remove-invalid-test
  (is (= [[7 3 47]] (c/remove-invalid parse-input-test-result))))

(deftest candidate-fields-test
  (is (= [["class" "row"] ["class"] ["seat"]] (c/candidate-fields parse-input-test-result [7 3 47]))))

(deftest reduce-to-single-test
  (is (= ["row" "class" "seat"] (c/reduce-to-single '([["row" "seat"] ["seat" "class"] ["row" "seat"]]
                                                      [["class" "row"] ["class"] ["seat"]])))))

(deftest sudoku-rules-test
  (is (= ["row" "class" "seat"] (c/sudoku-rules ['("row") '("class") '("seat")])))
  (is (= ["row" "class" "seat"] (c/sudoku-rules ['("row" "class") '("class" "seat") '("seat")]))))

(def p2data {"rules" {"departure class" [[0 1] [4 19]]
                      "departure row" [[0 5] [8 19]]
                      "seat" [[0 13] [16 19]]}
             "your ticket" [11 12 13]
             "nearby tickets" [[3 9 18]
                               [15 1 5]
                               [5 14 9]]})

(deftest part-2-test
  (is (= 132 (c/part-2 p2data))))