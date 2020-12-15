(ns d14.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [d14.core :as core]))

(def parse-input-test-result (try (with-open [r (io/reader "input.test.result.edn")]
                                    (clojure.edn/read (java.io.PushbackReader. r)))
                                  (catch java.io.IOException e
                                    (.println *err* (format "Couldn't open '%s': %s%n" "input.test.result.edn" (.getMessage e))))
                                  (catch RuntimeException e
                                    (.println *err* (format "Error parsing edn file '%s': %s%n" "input.test.result.edn" (.getMessage e))))))

(deftest parse-input-test
  (try (with-open [r (io/reader "input.test")]
         (is (= parse-input-test-result (core/parse-input (line-seq r)))))
       (catch java.io.IOException e
         (.println *err* (format "ERROR: Couldn't open '%s': %s%n" "input.test" (.getMessage e))))))

(deftest parse-line-test
  (is (= ["mask" nil "XXX10X"] (core/parse-line "mask = XXX10X")))
  (is (= ["mem" 3 4] (core/parse-line "mem[3] = 4"))))

(deftest and-mask-test
  (is (= 2r11111101 (core/and-mask "XXXXX10X"))))

(deftest or-mask-test
  (is (= 2r00000100 (core/or-mask "XXXXX10X"))))

(deftest apply-mask-test
  (map
   (fn [x y] (is (= y (core/apply-mask "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X" x))))
   [11 101 0] 
   [73 101 64]))

(deftest part-1-test
  (is (= 165 (core/part-1 parse-input-test-result))))