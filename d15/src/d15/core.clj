(ns d15.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (mapv #(Integer/parseInt %) (-> input
                                  (first)
                                  (str/split #","))))

(defn next-value
  "Given index, last value, and {last-values index-when}, return [next-index next-value trailing-map]"
  [input]
  (let [[last-idx last-value trailing-map] input]
    (let [new-idx (inc last-idx)
          last-time (get trailing-map last-value last-idx)
          new-value (- last-idx last-time)
          new-trailing-map (assoc trailing-map last-value last-idx)]
      [new-idx new-value new-trailing-map])))

(defn starting-map
  [starting-answers]
  (into {} (map-indexed #(vector %2 (inc %1)))  starting-answers))

(defn my-sequence
  [starting-answers]
  (apply conj (iterate next-value [(count starting-answers) (last starting-answers) (starting-map (butlast starting-answers))]) (reverse (butlast (map-indexed #(vector (inc %1) %2 {}) starting-answers)))))

(defn part-1
  [data]
  (second (nth (my-sequence data) 2019)))

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