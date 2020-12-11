(ns d10.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [d10.core]))

(def parse-input-test-result (try (with-open [r (io/reader "input.test.result.edn")]
                                    (sort (clojure.edn/read (java.io.PushbackReader. r))))
                                  (catch java.io.IOException e
                                    (.println *err* (format "Couldn't open '%s': %s%n" "input.test.result.edn" (.getMessage e))))
                                  (catch RuntimeException e
                                    (.println *err* (format "Error parsing edn file '%s': %s%n" "input.test.result.edn" (.getMessage e))))))

(deftest parse-input-test
  (try (with-open [r (io/reader "input.test")]
         (is (= parse-input-test-result (d10.core/parse-input (line-seq r)))))
       (catch java.io.IOException e
         (.println *err* (format "ERROR: Couldn't open '%s': %s%n" "input.test" (.getMessage e))))))

(deftest increment-seq-test
  (is (= '(1 1 1 3 3) (d10.core/increment-seq '(1 2 3 6))))
  (is (= {1 22, 3 10}) (frequencies (d10.core/increment-seq parse-input-test-result))))

(deftest part-1-test 
  (is (= [6 {1 3, 3 2}] (d10.core/part-1 '(1 2 3 6))))
  (is (= [220 {1 22, 3 10}] (d10.core/part-1 parse-input-test-result))))

(deftest n-permutations
  (map
    (fn [x y] (is (= x (d10.core/n-permutations y))))
   [1 2 3 4]
   [1 2 4 7]))

(deftest part-2-test
  (is (= 4 (d10.core/part-2 '(1 2 3 6))))
  (comment  '(0 1 2 3 6) '(0 1 3 6) '(0 2 3 6) '(0 3 6))
  (comment  '(1 1 1 3)   '(1 2 3)   '(2 1 3)   '(3 3))
  (is (= 8 (d10.core/part-2 '(1 4 5 6 7 10 11 12 15 16 19))))
  (comment '(0 1 4 5 6 7 10 11 12 15 16 19 22) '(0 1 4 5 6 7 10 12 15 16 19 22) '(0 1 4 5 7 10 11 12 15 16 19 22) '(0 1 4 5 7 10 12 15 16 19 22) '(0 1 4 6 7 10 11 12 15 16 19 22) '(0 1 4 6 7 10 12 15 16 19 22) '(0 1 4 7 10 11 12 15 16 19 22) '(0 1 4 7 10 12 15 16 19 22))
  (comment '(1 3 1 1 1 3  1  1  3  1  3  3)    '(1 3 1 1 1 3  2  3  1  3  3)    '(1 3 1 2 3  1  1  3  1  3  3)    '(1 3 1 2 3  2  3  1  3  3)     '(1 3 2 1 3  1  1  3  1  3  3)   '(1 3 2 1 3  2  3  1  3  3)    '(1 3 3 3  1  1  3  1  3  3)    '(1 3 3 3  2  3  1  3  3))
  (is (= 19208 (d10.core/part-2 parse-input-test-result))))