(ns d16.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]
   [clojure.set :as set]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (let [raw-string (str/join " " input)
        rules (into {} (mapv 
                        (fn [rule] 
                          (let [[_ key min-1 max-1 min-2 max-2] rule]
                            {key [[(Integer/parseInt min-1) (Integer/parseInt max-1)] [(Integer/parseInt min-2) (Integer/parseInt max-2)]]}))
                        (re-seq #"(\w[\w\s]+): (\d+)-(\d+) or (\d+)-(\d+)" raw-string)))
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
        rules (apply concat (vals (get data "rules")))
        invalid (remove #(valid-field? rules %) ticket-fields)]
    (reduce + invalid)))

(defn remove-invalid
  [data]
  (let [tickets (get data "nearby tickets")
        rules (apply concat (vals (get data "rules")))]
     (filter (fn [ticket] (every? #(valid-field? rules %) ticket)) tickets)))

(defn valid-fields 
  [rules n]
  (vec (keys (filter (fn [[_key rules]] (valid-field? rules n)) (seq rules))))
  )

(defn candidate-fields
  [data ticket]
  (mapv #(valid-fields (get data "rules") %) ticket))

(defn flatten-sets
  "Like flatten, but pulls elements out of sets instead of sequences."
  [v]
  (reverse (filter (complement set?)
                   (rest (tree-seq set? seq (set v))))))

(defn sudoku-rules
  [vec-of-lists]
  (loop [coll vec-of-lists]
    (if (every? #(= 1 (count %)) coll)
      (vec (flatten coll))
      (let [singles (apply set/union (filter #(= 1 (count %)) coll))]
        (recur (mapv #(if (= 1 (count %))
                        %
                        (vec (set/difference (set %) (set singles))))
                     coll))))))

(defn reduce-to-single
  [candidates-vecvec]
  (sudoku-rules 
   (mapv (fn [idx]
           (flatten-sets
            (reduce set/intersection (map #(set (nth % idx)) candidates-vecvec)))) 
    (range 0 (count (first candidates-vecvec))))))

(defn part-2
  "Given a sorted list of values, return the number of permutations of increments no greater than 3, leading to the (+ maximum-value 3)"
  [data]
  (let [your-ticket (get data "your ticket")
        valid-tickets (remove-invalid data)
        candidate-tickets (mapv #(candidate-fields data %) valid-tickets)
        ticket-ordering (reduce-to-single candidate-tickets)
        departure-fields  (mapv first (filter #(= "departure" (str/join (take 9 (last %)))) (map-indexed  #(vector %1 %2) ticket-ordering)))
        your-ticket-departure-fields (map #(nth your-ticket %) departure-fields)]
    (reduce * your-ticket-departure-fields)))

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
