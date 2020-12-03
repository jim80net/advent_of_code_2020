(ns d2
  (:require [clojure.core]
            [clojure.string]))

(defn input
  ([]
   (seq ["1-3 a: abcde"
         "1-3 b: cdefg"
         "2-9 c: ccccccccc"]))
  ([_stdin]
   (line-seq (java.io.BufferedReader. *in*))))

(defn parse_input
  [input]
  (map #(clojure.string/split % #" ") input))

(defn result
  [triple]
  (let [[s_range s_character s_sample] triple
        range (map #(Integer/parseInt %) (clojure.string/split s_range #"-"))
        min (first range)
        max (last range)
        character (first s_character)
        sample (vec s_sample)
        number (or (get (frequencies sample) character) 0)]
    (.println *err* (str
                     "min: " min
                     " max: " max 
                     " character: " character 
                     " sample: " sample 
                     " number: " number))
    (if (and (<= number max) (>= number min)) 1 0)))

(def main
  (let [outcome (map result (parse_input (input :stdin)))]
    (.println *err* (vec (take 20 outcome)))
    (println (str (reduce + outcome)))))
