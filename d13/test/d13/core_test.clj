(ns d13.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [d13.core]))

(def parse-input-test-result (try (with-open [r (io/reader "input.test.result.edn")]
                                    (clojure.edn/read (java.io.PushbackReader. r)))
                                  (catch java.io.IOException e
                                    (.println *err* (format "Couldn't open '%s': %s%n" "input.test.result.edn" (.getMessage e))))
                                  (catch RuntimeException e
                                    (.println *err* (format "Error parsing edn file '%s': %s%n" "input.test.result.edn" (.getMessage e))))))

(deftest parse-input-test
  (try (with-open [r (io/reader "input.test")]
         (is (= parse-input-test-result (d13.core/parse-input (line-seq r)))))
       (catch java.io.IOException e
         (.println *err* (format "ERROR: Couldn't open '%s': %s%n" "input.test" (.getMessage e))))))

(deftest next-time-test
  (is (= 12 (d13.core/next-time 10 3)))
  (is (= 12 (d13.core/next-time 11 2)))
  (is (= 13 (d13.core/next-time 11 13)))
  (is (= 12 (d13.core/next-time 12 3))))

(deftest part-1-test
  (is (= 295  (d13.core/part-1 parse-input-test-result))))

(deftest busses-sequential? 
  (is (true? (d13.core/busses-sequential? [0 1 2] [0 1 2])))
  (is (true? (d13.core/busses-sequential? [0 1 2] [0 ##Inf 2]))))

(deftest lcm-test
  (is (= 3 (d13.core/lcm 3)))
  (is (= 6 (d13.core/lcm 3 6)))
  (is (= 18 (d13.core/lcm 3 6 9)))
  (is (= 24 (d13.core/lcm 2 4 6 8 ))))

(deftest part-2-test
  (is (= 1 (d13.core/part-2 [0 [1 2 3]])))
  (is (= 3417 (d13.core/part-2 [0 [17 0 13 19]])))
  (is (= 754018 (d13.core/part-2 [0 [67 7 59 61]])))
  (is (= 779210 (d13.core/part-2 [0 [67 0 7 59 61]])))
  (is (= 1261476 (d13.core/part-2 [0 [67 7 0 59 61]])))
  (is (= 1202161486 (d13.core/part-2 [0 [1789 37 47 1889]]))))