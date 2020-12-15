(ns d14.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn maybe-number
  "Maybe-number returns either the Integer expressed in `s` or `s`."
  [s]
  (try
    (Integer/parseInt s)
    (catch java.lang.NumberFormatException _e s)))

(defn parse-line
  [line]
  (let [[action-s value-s] (str/split line #" = ")
        [_ action target-s] (re-matches #"(mask|mem)(?:\[(\d+)\])?" action-s)
        target (maybe-number target-s)
        value (maybe-number value-s)]
    [action target value]))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (map parse-line input))

(defn and-mask
  "Takes a mask String and returns the and-mask Binary, which if you apply with `clojure.core/bit-and`, realizes the 0's of the mask."
  [s]
  (-> s
      (str/replace #"^" "2r")
      (str/replace #"X" "1")
      (str/replace #"1" "1")
      (str/replace #"0" "0")
      (read-string)))

(defn or-mask
  "Takes a mask String and returns the or-mask Binary, which if you apply with `clojure.core/bit-or`, realizes the 0's of the mask."
  [s]  
  (-> s
      (str/replace #"^" "2r")
      (str/replace #"X" "0")
      (str/replace #"1" "1")
      (str/replace #"0" "0")
      (read-string)))

(defn apply-mask
  [mask n]
  (-> n
      (bit-and (and-mask mask))
      (bit-or (or-mask mask))))

(defn part-1
  [data]
  (loop [program data
         bitmask-s nil
         memory {}]
    (if (empty? program)
      (reduce + (vals memory))
      (let [[action target value] (first program)
            new-program (rest program)
            new-bitmask-s (if (= action "mask") value bitmask-s)
            new-memory (if (= action "mem") (assoc memory target (apply-mask bitmask-s value)) memory)]
        (recur new-program new-bitmask-s new-memory)))))

(defn floating-mask
  [mask n]
  (let [n-b (Integer/toBinaryString n)
        n-bs (reduce str (concat (repeat (- (count mask) (count n-b)) "0") n-b))]
    (loop [idx 0
           acc [""]]
      (if (> idx (dec (count mask)))
        (mapv #(read-string (str "2r" %)) acc)
        (let [mask-char (get mask idx)
              n-char (get n-bs idx)]
          (if (= mask-char \X)
            (recur (inc idx) (concat (map #(str % "0") acc) (map #(str % "1") acc)))
            (recur (inc idx) (mapv #(str % n-char) acc))))))))

(defn apply-mask-2
  [mask n] 
  (mapv #(bit-or (or-mask mask) %)
        (floating-mask mask n)))

(defn update-memory
  [memory keys value]
  (reduce #(assoc %1 %2 value) memory keys))

(defn part-2
  "Given a sorted list of values, return the number of permutations of increments no greater than 3, leading to the (+ maximum-value 3)"
  [data]
  (loop [program data
         bitmask-s nil
         memory {}]
    (if (empty? program)
      (reduce + (vals memory))
      (let [[action target value] (first program)
            new-program (rest program)
            new-bitmask-s (if (= action "mask") value bitmask-s)
            new-memory (if (= action "mem") (update-memory memory (apply-mask-2 bitmask-s target) value) memory)]
        (comment (.println *err* (str "recurring with " new-program new-bitmask-s new-memory) ))
        (recur new-program new-bitmask-s new-memory)))))

(defn -main
  [& args]
  (if (seq args)
    (let [data (parse-input input)
          p1-outcome (part-1 data)
          p2-outcome (part-2 data)]
      (println (str "part 1: " p1-outcome))
      (println (str "part 2: " p2-outcome)))
    nil))
