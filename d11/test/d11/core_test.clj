(ns d11.core-test
  (:require
   [clojure.edn]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is]]
   [d11.core]))

(defn parse-input-test-result
  ([] (parse-input-test-result "input.test.result0.edn"))
  ([filename]
   (try (with-open [r (io/reader filename)]
          (clojure.edn/read (java.io.PushbackReader. r)))
        (catch java.io.IOException e
          (.println *err* (format "Couldn't open '%s': %s%n" filename (.getMessage e))))
        (catch RuntimeException e
          (.println *err* (format "Error parsing edn file '%s': %s%n" filename (.getMessage e)))))))

(def test-step0 (parse-input-test-result "input.test.result0.edn"))
(def test-step1 (parse-input-test-result "input.test.result1.edn"))
(def test-step2 (parse-input-test-result "input.test.result2.edn"))
(def test-step3 (parse-input-test-result "input.test.result3.edn"))
(def test-step4 (parse-input-test-result "input.test.result4.edn"))
(def test-step5 (parse-input-test-result "input.test.result5.edn"))


(deftest parse-input-test
  (try (with-open [r (io/reader "input.test")]
         (is (= test-step0 (d11.core/parse-input (line-seq r)))))
       (catch java.io.IOException e
         (.println *err* (format "ERROR: Couldn't open '%s': %s%n" "input.test" (.getMessage e))))))

(deftest parse-char-test
  (map (fn [x y] (is (= (d11.core/parse-char x) y))) ["L" "#" "."] [-1 1 0]))

(deftest neighbors-test 
  (is (= '(0 1 2 3 4 5 6 7) (d11.core/neighbors [[0 1 2] [3 \. 4] [5 6 7]] [1 1])))
  (is (= '(0 2 3 \. 4) (d11.core/neighbors [[0 1 2] [3 \. 4] [5 6 7]] [0 1]))))

(deftest neighbors->count-test
  (is (= 4 (d11.core/neighbors->count '(1 1 1 1 -1 -1 -1 -1))))
  (is (= 2 (d11.core/neighbors->count '(-1 -1 0 0 0 0 1 1))))
  )

(deftest chair-swap!-test 
  (is (= 1 (d11.core/chair-swap! -1)))
  (is (= 0 (d11.core/chair-swap! 0)))
  (is (= -1 (d11.core/chair-swap! 1))))

(deftest swap?-test
  (is (d11.core/swap? test-step0 [0 2]))
  (is (false? (d11.core/swap? test-step1 [0 0]))))

(deftest step-test
  (is (= test-step1 (d11.core/step test-step0)))
  (is (= test-step2 (d11.core/step test-step1)))
  (is (= test-step3 (d11.core/step test-step2)))
  (is (= test-step4 (d11.core/step test-step3)))
  (is (= test-step5 (d11.core/step test-step4)))
  (is (= test-step5 (d11.core/step test-step5))))
    