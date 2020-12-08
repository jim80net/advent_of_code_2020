(ns d8
  (:require [clojure.core]
            [clojure.string]
            [clojure.set]))
(use 'clojure.test)


(def input
  (line-seq (java.io.BufferedReader. *in*)))


(defn parse_function
  [function_string]
  (let [[function_char & value_string_seq] function_string
        function (str function_char)
        value (Integer/parseInt (clojure.string/join value_string_seq))]
    (eval (read-string (str "#(" function " % " value ")" )))))

(deftest parse_function_test (is (= 2 ((parse_function "+1") 1))))


(defn parse_command
  [command_string]
  (let [ab (clojure.string/split command_string #" ")
        action (first ab)
        function (parse_function (last ab))]
    [action function]))

(deftest parse_command_test (is (= 2 ((last (parse_command "nop +1")) 1))))


(defn parse_input
  "Return a hash-map of bag to constiuent bags as a sequence of hash-maps or sequence of nil."
  [input]
  (mapv parse_command input))

(def parse_input_test_result_reference [["nop" #(+ % 0)]
                                        ["acc" #(+ % 1)]
                                        ["jmp" #(+ % 4)]
                                        ["acc" #(+ % 3)]
                                        ["jmp" #(- % 3)]
                                        ["acc" #(- % 99)]
                                        ["acc" #(+ % 1)]
                                        ["jmp" #(- % 4)]
                                        ["acc" #(+ % 6)]])
(def parse_input_test_result [1 2 5 4 -2 -98 2 -3 7])
(deftest parse_input_test 
  (is (= parse_input_test_result
         (mapv (fn [x] ((last x) 1)) (parse_input (seq ["nop +0"
                                  "acc +1"
                                  "jmp +4"
                                  "acc +3"
                                  "jmp -3"
                                  "acc -99"
                                  "acc +1"
                                  "jmp -4"
                                  "acc +6"]))))))


(defn visited?
  "Predicate which returns true if the node v has been visited already, false otherwise."
  [v coll]
  (some #(= % v) coll))


(defn part_1
  [command_vec]
  (loop [command_idx 0
         visited #{}
         acc 0]
    (if (or (visited? command_idx visited) (> command_idx (dec (count command_vec))))
      [acc command_idx]
      (let [command (nth command_vec command_idx)
            action (first command)
            function (last command)]
        (case action
          "nop" (recur (inc command_idx) (conj visited command_idx) acc)
          "jmp" (recur (function command_idx) (conj visited command_idx) acc)
          "acc" (recur (inc command_idx) (conj visited command_idx) (function acc)))))))

(deftest part_1_test (is (= [5 1] (part_1 parse_input_test_result_reference) )))


(defn swap 
  [[action function]]
  (case action
    "nop" ["jmp" function]
    "jmp" ["nop" function]
    [action function]))

(deftest swap_test (is (= ["jmp" identity] (swap ["nop" identity]))))


(defn part_2
  [command_vec]
  (loop [conversion_idx 0]
    (let [new_command (swap (nth command_vec conversion_idx))
          new_command_vec (assoc command_vec conversion_idx new_command)
          new_result (part_1 new_command_vec)
          new_acc (first new_result)
          new_last_command (last new_result)]
      (if (or (> new_last_command (dec (count command_vec))) (> conversion_idx (dec (count command_vec))))
        new_acc
        (recur (inc conversion_idx))))))


(def main
  (let [rules (parse_input input)
        p1_outcome (part_1 rules)
        p2_outcome (part_2 rules)]
    (run-tests 'd8)
    (println (str "part 1 :" (first p1_outcome) " on index: " (last p1_outcome)))
    (println (str "part 2 :" p2_outcome))))
