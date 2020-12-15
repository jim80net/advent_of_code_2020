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
  (is (= 2r00000100 (core/or-mask "XXXXX10X")))
  (is (= 2r010010 (core/or-mask "X1001X"))))

(deftest apply-mask-test
  (map
   (fn [x y] (is (= y (core/apply-mask "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X" x))))
   [11 101 0] 
   [73 101 64]))

(deftest part-1-test
  (is (= 165 (core/part-1 parse-input-test-result))))

(deftest floating-mask-test
  (is (= [2r00 2r10 2r01  2r11] (core/floating-mask "XX" 0)))
  (is (= [2r011010 2r111010 2r011011  2r111011] (core/floating-mask "X1001X" 2r111010)))
  (is (= [16 24 18 26 17 25 19 27] (core/floating-mask "00000000000000000000000000000000X0XX" 26))))

(deftest apply-mask2-test
  (is (= [26 58 27 59] (core/apply-mask-2 "000000000000000000000000000000X1001X" 2r000000000000000000000000000000101010))))

(deftest update-memory-test
  (is (= {:a 1 :b 1 :c 1} (core/update-memory {} [:a :b :c] 1)))
  (is (= {:a 1 :b 1 :c 1 :d 2} (core/update-memory {:d 2} [:a :b :c] 1))))

(deftest part-2-test
  (is (= 208 (core/part-2 [["mask" nil "000000000000000000000000000000X1001X"]
                           ["mem" 42 100]
                           ["mask" nil "00000000000000000000000000000000X0XX"]
                           ["mem" 26 1]]))))
