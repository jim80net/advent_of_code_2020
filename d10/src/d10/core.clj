(ns d10.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (sort (map #(Integer/parseInt %) input)))

(defn increment-seq
  "Given a set of adapters, return the full sequence (from outlet to device) of increments between all the adapters"
  [data]
  (conj (mapv - data (conj data 0)) 3))

(defn part-1
  [data]
  (let [freq (frequencies (conj (mapv - data (conj data 0)) 3))
        multiple (reduce * (vals freq))]
    [multiple freq]))

(defn n-permutations 
  "http://mathforum.org/library/drmath/view/61212.html"
  [n]
  (inc (/ (* n (dec n)) 2)))

(defn part-2
  "Given a sorted list of values, return the number of permutations of increments no greater than 3, leading to the (+ maximum-value 3)"
  [data]
  (loop [my-seq (increment-seq data)
         acc '()]
    (if (empty? my-seq)
      (reduce * acc)
      (let [sans-leading-3s (drop-while (partial = 3) my-seq)
            contiguous-1s (take-while (partial = 1) sans-leading-3s)
            remainder (drop-while (partial = 1) sans-leading-3s)
            contiguous-count  (count contiguous-1s)]
        (if (< 1 contiguous-count)
          (recur remainder (conj acc (n-permutations contiguous-count)))
          (recur remainder acc))))))


(defn -main
  [& args]
  (if (seq args)
    (let [data (parse-input input)
          p1-outcome (part-1 data)
          p2-outcome (part-2 data)]
      (println (str "part 1 : " (first p1-outcome) " from frequency table: " (last p1-outcome)))
      (println (str "part 2 : " p2-outcome)))
    nil))
