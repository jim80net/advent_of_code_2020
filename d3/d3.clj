(ns d3
  (:require [clojure.core]
            [clojure.string]))

(defn input
  ([]
   (seq ["..##......."
         "#...#...#.."
         ".#....#..#."
         "..#.#...#.#"
         ".#...##..#."
         "..#.##....."
         ".#.#.#....#"
         ".#........#"
         "#.##...#..."
         "#...##....#"
         ".#..#...#.#"]))
  ([_stdin]
   (line-seq (java.io.BufferedReader. *in*))))

(defn parse_input
  [input]
  (mapv #(clojure.string/split % #"") input))

(defn element_at
  [vecvec w_index h_index] 
  (.println *err* (str "searching for " w_index "," h_index))
  (let [row (get vecvec h_index) element (get row w_index)]
    (.println *err* (str "found: " element " in " row)) 
    (get (get vecvec h_index) w_index)))

(defn elements
  [vecvec w_slope h_slope]
  (loop [vecvec vecvec
         w_start_pos 0
         h_start_pos 0
         w_slope w_slope
         h_slope h_slope
         acc [(element_at vecvec 0 0)]]
    (let [width (count (first vecvec))
          height (count vecvec)
          w_end_pos (+ w_start_pos w_slope)
          h_end_pos (+ h_start_pos h_slope)
          w_index (mod w_end_pos width)
          h_index h_end_pos]
      (if (> h_index height)
        acc
        (recur
         vecvec
         w_index
         h_index
         w_slope
         h_slope
         (conj acc (element_at vecvec w_index h_index)))))))

(defn trees
  [path]
  (get (frequencies path) "#"))

(def main_p1
  (let [outcome (trees (elements (parse_input (input :stdin)) 3 1))]
    (println (str outcome))))

(def main
  (let [vecvec (parse_input (input :stdin))
        o1 (trees (elements vecvec 1 1))
        o2 (trees (elements vecvec 3 1))
        o3 (trees (elements vecvec 5 1))
        o4 (trees (elements vecvec 7 1))
        o5 (trees (elements vecvec 1 2))]
    (println (str (reduce * [o1 o2 o3 o4 o5])))))
