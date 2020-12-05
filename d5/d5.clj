(ns d5
  (:require [clojure.core]
            [clojure.string]))

(defn input
  ([]
   (seq ["FBFBBFFRLR"
         "BFFFBBFRRR"
         "FFFBBBFRRR"
         "BBFFBBFRLL"]))
  ([_stdin]
   (line-seq (java.io.BufferedReader. *in*))))

(defn parse_input
  [input]
  (map (fn [boarding_pass] (->
                            boarding_pass
                            (clojure.string/replace "F" "0")
                            (clojure.string/replace "B" "1")
                            (clojure.string/replace "L" "0")
                            (clojure.string/replace "R" "1")
                            )) input))

(defn string_to_binary
  [string]
    (read-string (str "2r" string)))

#_
(def main_p1
  (let [outcome (map string_to_binary (parse_input (input :stdin)))]
    (println (str (apply max outcome)))))


(def main
  (let [outcome (map string_to_binary (parse_input (input :stdin)))]
    (println (loop [acc (sort (vec outcome))]
               (let [left (first acc)
                     right (first (rest acc))]
                 (if (= 2 (- right left))
                   (inc left)
                   (recur (rest acc))))))))