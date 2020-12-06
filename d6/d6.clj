(ns d6
  (:require [clojure.core]
            [clojure.string]
            [clojure.set]))

(defn input
  ([]
   (seq ["abc"
         ""
         "a"
         "b"
         "c"
         ""
         "ab"
         "ac"
         ""
         "a"
         "a"
         "a"
         "a"
         ""
         "b"]))
  ([_stdin]
   (line-seq (java.io.BufferedReader. *in*))))

(defn parse_input
  [input]
  (->> input
       (partition-by #(= "" %))
       (filter (fn [x] (not (= '("") x))))
       ))

(defn part_1
  [sequence]
  (map (fn [x] (->> x
                    (mapcat (comp seq char-array))
                    set
                    count))
       sequence))

(defn part_2
  [sequence]
  (map (fn [x] (->> x
                    (map (comp set seq char-array))
                    (reduce clojure.set/intersection)
                    count))
       sequence))

(def main
  (let [outcome   (reduce + (part_2 (parse_input (input :stdin))))]
    (println (str outcome))))
