(ns d9
  (:require [clojure.core]
            [clojure.string]
            [clojure.edn]
            [clojure.java.io]))
(use 'clojure.test)


(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (map #(Long/parseLong %) input))

(def parse-input-test-result (try (with-open [r (clojure.java.io/reader "input.test.result.edn")]
                                    (clojure.edn/read (java.io.PushbackReader. r)))
                                  (catch java.io.IOException e
                                    (.printf *err* "Couldn't open '%s': %s%\n" "input.test.result.edn" (.getMessage e)))
                                  (catch RuntimeException e
                                    (.printf *err* "Error parsing edn file '%s': %s%\n" "input.test.result.edn" (.getMessage e)))))
(deftest parse-input-test
  (is (= parse-input-test-result (parse-input (line-seq (clojure.java.io/reader "input.test"))))))


(defn valid?
  "Predicate which returns true if the node v is the sum of two of the members of the coll"
  [x coll]
  (some? (some
          (fn [y]
            (some #(= % (- x y)) (remove #{y} coll)))
          coll)))

(deftest valid?-test
  (is (valid? 26 (range 1 26)))
  (is (valid? 49 (range 1 26)))
  (is (not (valid? 50 (range 1 26))))
  (is (not (valid? 127 [95 102 117 150 182])))
  (is (valid? 3 [1 2]))
  (is (not (valid? 3 [1 1])))
  (is (valid? 47 [35 20 15 25 12])))


(defn part-1
  [rules preamble-length]
  (loop [lookup-table  (take preamble-length rules)
         remainder (drop preamble-length rules)]
    (if (valid? (first remainder) lookup-table)
      (recur (concat (rest lookup-table) (list (first remainder))) (rest remainder))
      [(first remainder) lookup-table])))

(deftest part-1-test
  (is (= [47 '(35 20 15 25 10)] (part-1 '(35 20 15 25 10 47) 5)))
  (is (= [15 '(35 20)] (part-1 '(35 20 15 25 10 47) 2))))

(defn seq-o-nums
  [x coll]
  (loop [start-idx 0
         length 2]
    (if (or (>= start-idx (dec (count coll))) (> length (- (dec (count coll)) start-idx)))
      nil
      (let [my-seq-o-nums (take length (drop start-idx coll))
            tally (reduce + my-seq-o-nums)]
        (cond
          (< tally x) (recur start-idx (inc length))
          (= tally x) my-seq-o-nums
          (> tally x) (recur (inc start-idx) 2))))))

(deftest seq-o-nums-test
  (is (= [1 2] (seq-o-nums 3 [1 2 3 4 5])))
  (is (= [1 2 3] (seq-o-nums 6 [1 2 3 4 5])))
  (is (= nil (seq-o-nums 13 [1 2 3 4 5])))
  (is (= nil (seq-o-nums 13 [1 2 3 4 5 10]))))


(defn part-2
  [rules preamble-length]
  (let [invalid-number (first (part-1 rules preamble-length))
        contiguous-seq (seq-o-nums invalid-number rules)]
    (+ (apply min contiguous-seq) (apply max contiguous-seq))))


(def main
  (let [rules (parse-input input)
        p1-outcome (part-1 rules 25)
        p2-outcome (part-2 rules 25)]
    (run-tests 'd9)
    (println (str "part 1 : " (first p1-outcome) " not a sum of any 2 in " (vec (last p1-outcome))))
    (println (str "part 2 : " p2-outcome))))
