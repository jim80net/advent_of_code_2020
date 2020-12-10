(ns d10
  (:require [clojure.core]
            [clojure.string :as str]
            [clojure.edn]
            [clojure.java.io :as io]
            [clojure.core.matrix :as mat]))
(use 'clojure.test)


(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (sort (map #(Integer/parseInt %) input)))

(def parse-input-test-result (try (with-open [r (io/reader "input.test.result.edn")]
                                    (sort (clojure.edn/read (java.io.PushbackReader. r))))
                                  (catch java.io.IOException e
                                    (.printf *err* "Couldn't open '%s': %s%\n" "input.test.result.edn" (.getMessage e)))
                                  (catch RuntimeException e
                                    (.printf *err* "Error parsing edn file '%s': %s%\n" "input.test.result.edn" (.getMessage e)))))
(deftest parse-input-test
  (is (= parse-input-test-result (parse-input (line-seq (io/reader "input.test"))))))



(defn part-1
  [data]
  (mat/- (conj data 0) data))

(deftest part-1-test (is (= [1 1 1 3] (part-1 '(1 2 3 6)))))

(defn part-2
  [data]
  data)


(def main
  (let [data (parse-input input)
        p1-outcome (part-1 data)
        p2-outcome (part-2 data)]
    (run-tests 'd10)
    (println (str "part 1 : " p1-outcome))
    (println (str "part 2 : " p2-outcome))))
