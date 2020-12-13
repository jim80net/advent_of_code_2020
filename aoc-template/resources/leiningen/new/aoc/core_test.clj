(ns {{name}}.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [{{name}}.core]))

(def parse-input-test-result (try (with-open [r (io/reader "input.test.result.edn")]
                                    (clojure.edn/read (java.io.PushbackReader. r)))
                                  (catch java.io.IOException e
                                    (.println *err* (format "Couldn't open '%s': %s%n" "input.test.result.edn" (.getMessage e))))
                                  (catch RuntimeException e
                                    (.println *err* (format "Error parsing edn file '%s': %s%n" "input.test.result.edn" (.getMessage e))))))

(deftest parse-input-test
  (try (with-open [r (io/reader "input.test")]
         (is (= parse-input-test-result ({{name}}.core/parse-input (line-seq r)))))
       (catch java.io.IOException e
         (.println *err* (format "ERROR: Couldn't open '%s': %s%n" "input.test" (.getMessage e))))))