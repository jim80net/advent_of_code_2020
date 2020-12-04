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
  [vecvec]
  (loop [vecvec vecvec
         w_start_pos 0
         h_start_pos 0
         w_slope 3
         h_slope 1
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

(def main
  (let [outcome (trees (elements (parse_input (input :stdin))))]
    (println (str outcome))))
