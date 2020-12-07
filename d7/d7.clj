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
    {nil 0}
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

(defn relationships_from_count_map
  [count_map]
  (loop [count_map_seq (seq count_map)
         parent_child_relationships #{}
         child_parent_relationships #{}]
    (if (empty? count_map_seq)
      [parent_child_relationships child_parent_relationships]
      (let [parent_and_children (first count_map_seq)
            parent (first parent_and_children)
            children (map (comp first first) (first (rest parent_and_children)))
            new_parent_child_relationships (clojure.set/union parent_child_relationships (set (map (fn [child] [parent child]) children)))
            new_child_parent_relationships (clojure.set/union child_parent_relationships (set (map (fn [child] [child parent]) children)))]
        (recur (rest count_map_seq) new_parent_child_relationships new_child_parent_relationships)))))

(defn adjacency_map_from_relationships
  [relationships]
  (loop [relationships relationships
        adjacency_map {}]
    (if (empty? relationships)
      (do  (comment (.println *err* (str "Adjacency map: " adjacency_map)))
           adjacency_map)
      (recur (rest relationships) (let [entry (first relationships)
                                           parent (first entry)
                                           child (last entry)]
                                       (assoc adjacency_map parent (if (get adjacency_map parent)
                                                                     (conj (get adjacency_map parent) child)
                                                                     [child])))))))

(defn adjacency_maps
  [count_map]
  (comment (.println *err* (str "Producing adjacency list for " count_map)))
  (let [[parent_child_relationships child_parent_relationships] (relationships_from_count_map count_map) 
        parent_child_adjacency_map (adjacency_map_from_relationships parent_child_relationships)
        child_parent_adjacency_map (adjacency_map_from_relationships child_parent_relationships)]
  [parent_child_adjacency_map child_parent_adjacency_map]))

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
            not_visited (filter (complement #(visited? % visited)) neighbors)
            new_stack   (into (pop stack) not_visited)]
        (if (or (visited? v visited) (nil? v))
          (recur new_stack visited)
          (recur new_stack (conj visited v)))))))

(defn part_1
  [count_map]
  (let [a_map (first (adjacency_maps count_map))
        shiny (graph-dfs a_map "shiny gold")]
    (.println *err* (str "Shiny gold: " shiny)) 
    (dec (count shiny))))

(defn part_2
  [count_map]
  (loop [stack [{"shiny gold" 1}]
         running_bag_count 0]
    (.println *err* (str "looking at " stack))
    (if (empty? stack)
      (inc running_bag_count)
      (let [my_bag (first (first (peek stack)))
            my_bag_count (last (first (peek stack)))
            my_new_bag_count (dec my_bag_count)
            inside_bags (vec (get count_map my_bag))
            new_stack (into (pop stack) inside_bags)]

        (.println *err* (str "i have " my_bag_count " " my_bag " bag(s). They hold " inside_bags ". Running total: " running_bag_count ))
        (if (> my_new_bag_count 1)
            (recur (conj new_stack {my_bag my_new_bag_count}) (inc running_bag_count))
            (recur new_stack (inc running_bag_count)))))))

(def main
  (let [outcome   (part_2 (parse_input input))]
    (println (str outcome))))
