(ns d14.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-line
  [line]
  (let [[action-s value-s] (str/split line #" = ")
        [_ action target-s] (re-matches #"(mask|mem)(?:\[(\d+)\])?" action-s)
        target (try
                 (Integer/parseInt target-s)
                 (catch java.lang.NumberFormatException _e target-s))
        value (try
                (Integer/parseInt value-s)
                (catch java.lang.NumberFormatException _e value-s))]
    [action target value]))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (map parse-line input))

(defn part-1
  [data]
  data)

(defn part-2
  "Given a sorted list of values, return the number of permutations of increments no greater than 3, leading to the (+ maximum-value 3)"
  [data]
  data)

(defn -main
  [& args]
  (if (seq args)
    (let [data (parse-input input)
          p1-outcome (part-1 data)
          p2-outcome (part-2 data)]
      (println (str "part 1: " (first p1-outcome) " from: " (last p1-outcome)))
      (println (str "part 2: " p2-outcome)))
    nil))
cat
