(ns d15.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [d15.core :as c]))

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

(deftest next-value-test
  (is (= [4 0 {0 1, 3 2, 6 3}] (c/next-value [3 6 {0 1, 3 2}])))
  (is (= [5 3 {0 4, 3 2, 6 3}] (c/next-value [4 0 {0 1, 3 2, 6 3}])))
  (is (= [6 3 {0 4, 3 5, 6 3}] (c/next-value [5 3 {0 4, 3 2, 6 3}])))
  (is (= [7 1 {0 4, 3 6, 6 3}] (c/next-value [6 3 {0 4, 3 5, 6 3}])))
  (is (= [8 0 {0 4, 3 6, 6 3, 1 7}] (c/next-value [7 1 {0 4, 3 6, 6 3}])))
  (is (= [9 4 {0 8, 3 6, 6 3, 1 7}] (c/next-value [8 0 {0 4, 3 6, 6 3, 1 7}])))
  (is (= [10 0 {0 8, 3 6, 6 3, 1 7, 4 9}] (c/next-value [9 4 {0 8, 3 6, 6 3, 1 7}]))))

(deftest starting-map-test
  (is (= {0 1, 3 2, 6 3} (c/starting-map [0 3 6]))))

(deftest my-sequence-test
  (doall (map
          (fn [x y] (is (= y (second (nth (c/my-sequence [0 3 6]) (dec x))))))
          [1 2 3 4 5 6 7 8 9 10]
          [0 3 6 0 3 3 1 0 4 0])))

(deftest part-1-test
  (is (= 1 (c/part-1 [1 3 2])))
  (is (= 10 (c/part-1 [2 1 3])))
  (is (= 27 (c/part-1 [1 2 3])))
  (is (= 78 (c/part-1 [2 3 1])))
  (is (= 438 (c/part-1 [3 2 1])))
  (is (= 1836 (c/part-1 [3 1 2]))))