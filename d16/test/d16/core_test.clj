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
  (get parse-input-test-result "rules"))

(deftest valid-field?-test
  (is (true? (c/valid-field? rules 7)))
  (is (true? (c/valid-field? rules 3)))
  (is (true? (c/valid-field? rules 47)))
  (is (false? (c/valid-field? rules 4)))
  (is (false? (c/valid-field? rules 55)))
  (is (false? (c/valid-field? rules 12))))

(deftest part-1-test
  (is (= 11172 (c/part-1 parse-input-test-result))))