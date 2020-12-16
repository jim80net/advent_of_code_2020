(ns d16.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (let [raw-string (str/join " " input)
        rules (mapv (fn [s] (mapv #(Integer/parseInt %) (str/split s #"-"))) (re-seq #"\d+-\d+" raw-string))
        tickets (mapv (fn [s] (mapv #(Integer/parseInt %) (str/split (str/trim s) #","))) (re-seq  #" (?:\d+\,)+\d+" raw-string))
        your-ticket (first tickets)
        nearby-tickets (vec (rest tickets))]
    {"rules" rules, "your ticket" your-ticket, "nearby tickets" nearby-tickets}))

(defn valid-field?
  [rules n]
  (some? (some (fn [[x y]] (and (>= n x) (<= n y))) rules)))

(defn part-1
  [data]
  (let [ticket-fields (flatten (get data "nearby tickets"))
        rules (get data "rules")
        invalid (remove #(valid-field? rules %) ticket-fields)]
    (reduce + invalid)))

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
      (println (str "part 1: " p1-outcome))
      (println (str "part 2: " p2-outcome)))
    nil))
cat
