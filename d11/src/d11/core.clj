(ns d11.core
  (:refer-clojure)
  (:require
   [clojure.string :as str]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse-char [char] (case char "L" -1 "#" 1 "." 0))

(defn parse-input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (mapv (fn [x] (mapv parse-char (str/split x #""))) input))

(defn neighbors
  "Evaluates a coordinate and returns a list of neighbors"
  [neighbors-vecvec coord]
   (let [[r c] coord]
     (remove nil? (map #(get-in neighbors-vecvec %) [[(dec r) (dec c)]
                                                     [(dec r) c]
                                                     [(dec r) (inc c)]
                                                     [r (dec c)]
                                                     [r (inc c)]
                                                     [(inc r) (dec c)]
                                                     [(inc r) c]
                                                     [(inc r) (inc c)]]))))

(defn neighbors->count
  "Takes a list of neighbors and returns a count of occupied seats"
  [neighbors]
  (reduce + (filter (partial = 1) neighbors)))


(defn chair-swap!
  "Takes a value and returns its inverse (occupied->unoccupied, floor->floor, occupied->unoccupied)"
  [x]
  (case x
    -1 1
    0 0
    1 -1))

(defn swap?
  "Evaluates a coordinate and returns a boolean"
  [neighbors-vecvec coord]
  (let [current (get-in neighbors-vecvec coord)
        neighbors-count (neighbors->count (neighbors neighbors-vecvec coord))]
    (or
     (and
      (= current -1)
      (= 0 neighbors-count))
     (and
      (= current 1)
      (<= 4 neighbors-count)))))


  
(defn swap-if-necessary-chair
  "Evaluates a coordinate and returns the correct chair"
  [neighbors-vecvec coord]
  (let [current (get-in neighbors-vecvec coord)
        should-i? (swap? neighbors-vecvec coord)]
    (if should-i? (chair-swap! current) current)))



(defn step
  [neighbors-vecvec]
  (vec (map-indexed (fn [row-idx row]
                      (vec (map-indexed (fn [chair-idx _chair]
                                          (swap-if-necessary-chair neighbors-vecvec [row-idx chair-idx]))
                                        row)))
                    neighbors-vecvec)))

(defn part-1
  [data]
  (loop [neighbors-vecvec data]
    (let [next-vecvec (step neighbors-vecvec)]
      (if (= neighbors-vecvec next-vecvec)
        [(count (filter (partial = 1) (flatten neighbors-vecvec))) neighbors-vecvec]
        (recur next-vecvec)))))

(defn line-of-sight
  [neighbors-vecvec delta starting-coord]
  (let [max-rows (dec (count neighbors-vecvec))
        max-columns (dec (count (first neighbors-vecvec)))
        delta-r (first delta)
        delta-c (last delta)
        starting-r (first starting-coord)
        starting-c (last starting-coord)]
    (loop [my-r (delta-r starting-r)
           my-c (delta-c starting-c)]
      (cond
        (> my-r max-rows) 0
        (> my-c max-columns) 0
        (< my-r 0) 0
        (< my-c 0) 0
        :else (let [my-seat (get-in neighbors-vecvec [my-r my-c])]
                (cond
                  (pos? my-seat) my-seat
                  (neg? my-seat) my-seat
                  :else (recur (delta-r my-r) (delta-c my-c))))))))



(defn part-2-neighbors
  [neighbors-vecvec coord]
     (let [[r c] coord]
       (map #(line-of-sight neighbors-vecvec % coord) [[dec dec]
                                                       [dec identity]
                                                       [dec inc]
                                                       [identity dec]
                                                       [identity inc]
                                                       [inc dec]
                                                       [inc identity]
                                                       [inc inc]])))


(defn part2-swap?
  "Evaluates a coordinate and returns a boolean"
  [neighbors-vecvec coord]
  (let [current (get-in neighbors-vecvec coord)
        neighbors-count (neighbors->count (part-2-neighbors neighbors-vecvec coord))]
    (or
     (and
      (= current -1)
      (= 0 neighbors-count))
     (and
      (= current 1)
      (<= 5 neighbors-count)))))

(defn part2-swap-if-necessary-chair
  "Evaluates a coordinate and returns the correct chair"
  [neighbors-vecvec coord]
  (let [current (get-in neighbors-vecvec coord)
        should-i? (part2-swap? neighbors-vecvec coord)]
    (if should-i? (chair-swap! current) current)))

(defn part2-step
  [neighbors-vecvec]
  (vec (map-indexed (fn [row-idx row]
                      (vec (map-indexed (fn [chair-idx _chair]
                                          (part2-swap-if-necessary-chair neighbors-vecvec [row-idx chair-idx]))
                                        row)))
                    neighbors-vecvec)))

(defn part-2
  "Given a sorted list of values, return the number of permutations of increments no greater than 3, leading to the (+ maximum-value 3)"
  [data]
  (loop [neighbors-vecvec data]
    (let [next-vecvec (part2-step neighbors-vecvec)]
      (if (= neighbors-vecvec next-vecvec)
        [(count (filter (partial = 1) (flatten neighbors-vecvec))) neighbors-vecvec]
        (recur next-vecvec)))))

(defn -main
  [& args]
  (if (seq args)
    (let [data (parse-input input)
          p1-outcome (part-1 data)
          p2-outcome (part-2 data)]
      (println (str "part 1: " (first p1-outcome) (comment " from: " (last p1-outcome))))
      (println (str "part 2: " (first p2-outcome) (comment (last p2-outcome)))))
    nil))
