(ns d6
  (:require [clojure.core]
            [clojure.string]
            [clojure.set]))

(def input
  (line-seq (java.io.BufferedReader. *in*)))

(defn parse_outer
  [string]
  (clojure.string/replace string #" bag(?:s)?" ""))


(defn parse_inner_bag
  [bag]
  (if (= "no other bags."  bag)
    nil
    (let [sequence (rest (re-matches #"(\d+) ([\w ]+) bag(?:s)?(?:\.)?" bag))
          quantity (Integer/parseInt (first sequence))
          color (first (rest sequence))]
      {color quantity})))

(defn parse_inner
  [string]
  (map parse_inner_bag (clojure.string/split string #", ")))


(defn parse_line
  [line]
  (let [head_and_tail (clojure.string/split line #" contain ")
        outer (parse_outer (first head_and_tail))
        inner (parse_inner (first (rest head_and_tail)))]
     [outer inner]))

(defn parse_input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (let [outer_inners (map parse_line input)]
  (into {} (mapv #(apply hash-map %) outer_inners))))

(defn adjacency_map
  [count_map]
  (comment (.println *err* (str "Producing adjacency list for " count_map)))
  (loop [relationship_set (loop [count_map_seq (seq count_map)
                                relationships #{}]
                           (if (empty? count_map_seq)
                             relationships
                             (let [parent_and_children (first count_map_seq)
                                   parent (first parent_and_children)
                                   children (map (comp first first) (first (rest parent_and_children)))
                                   new_relationships (clojure.set/union relationships (set (map (fn [child] [child parent]) children)))]
                               (recur (rest count_map_seq) new_relationships))))
        adjacency_map {}]
    (if (empty? relationship_set)
      (do   (.println *err* (str "Adjacency map: " adjacency_map))
           adjacency_map)
      (recur (rest relationship_set) (let [entry (first relationship_set)
                                           parent (first entry)
                                           child (last entry)]
                                       (assoc adjacency_map parent (if (get adjacency_map parent)
                                                                     (conj (get adjacency_map parent) child)
                                                                     [child])))))))

(defn visited?
  "Predicate which returns true if the node v has been visited already, false otherwise."
  [v coll]
  (some #(= % v) coll))

(defn graph-dfs
  "Traverses a graph in Depth First Search (DFS)"
  [graph v]
  (loop [stack   (vector v) ;; Use a stack to store nodes we need to explore
         visited []]        ;; A vector to store the sequence of visited nodes
    (if (empty? stack)      ;; Base case - return visited nodes if the stack is empty
      visited
      (let [v           (peek stack)
            neighbors   (get graph v)
            not-visited (filter (complement #(visited? % visited)) neighbors)
            new-stack   (into (pop stack) not-visited)]
        (if (or (visited? v visited) (nil? v))
          (recur new-stack visited)
          (recur new-stack (conj visited v)))))))

(defn part_1
  [count_map]
  (let [a_map (adjacency_map count_map)
        shiny (graph-dfs a_map "shiny gold")]
    (.println *err* (str "Shiny gold: " shiny)) 
    (dec (count shiny))))


(def main
  (let [outcome   (part_1 (parse_input input))]
    (println (str outcome))))
