(ns d6
  (:require [clojure.core]
            [clojure.string]))

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
       (map (fn [x] (->> x (mapcat (comp seq char-array)) set count)))))    


(def main
  (let [outcome  (apply + (parse_input (input :stdin)))]
    (println (str outcome))))
