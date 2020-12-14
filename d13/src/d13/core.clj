(ns d13.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (let [earliest-time (Integer/parseInt (first input))
        busses (mapv #(Integer/parseInt %) (str/split (str/replace (second input) #"x" "0") #","))]
    [earliest-time busses]))

(defn next-time
  "Or now, if the bus is departing on this tick."
  [minimum interval]
  (if (> interval 0) 
    (let [remainder (mod minimum interval)]
      (if (= 0 remainder)
        minimum
        (+ minimum (- interval remainder))))
    ##Inf))

(defn part-1
  [data]
  (let [earliest-time (first data)
        busses (second data)
        next-busses (mapv #(next-time earliest-time %) busses)
        least-index (first (apply min-key second (map-indexed vector next-busses)))
        next-time (get next-busses least-index)
        wait-time (- next-time earliest-time)
        bus-number (get busses least-index)]
    (.println *err* (str "From: " earliest-time ", the next bus: " bus-number ", arrives at " next-time ". The wait time is " wait-time))
    (* wait-time bus-number)))

(defn busses-sequential?
  [template test]
      (every? true? (map (fn [template test-value] 
                                   (or 
                                    (= ##Inf test-value) 
                                    (= template test-value))) 
                                  template test)))

(defn gcd
  [a b]
  (if (zero? b)
    a
    (recur b, (mod a b))))

(defn lcm
  ([a] a)
  ([a b]
   (/ (* a b) (gcd a b)))
  ([a b & v]  (lcm a (lcm b (reduce lcm v)))))


(defn part-2
  "Given a sorted list of values, return the number of permutations of increments no greater than 3, leading to the (+ maximum-value 3)"
  [data]
  (let [busses (second data)
        template (vec (range 0 (count busses)))
        ceiling (apply * (remove #(= 0 %) busses))]
    (loop [time 1]
      (let [next-busses (mapv (fn [[idx bus]] (next-time (+ time idx) bus)) (map-indexed vector busses))
            delta-between (mapv #(- % time) next-busses)
            offset-between (mapv - delta-between template)
            offset (reduce lcm 1 (take (count (take-while #(= 0 %) offset-between) ) busses))]
        
        (if (or (busses-sequential? template delta-between) (< ceiling time))
          (do
            (.println *err* (str "Time: " time "/" ceiling ". The distance between the next-busses: " next-busses " is: " delta-between ". The offset is: " offset-between))
            time)
          (recur (+ time offset)))))))

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
