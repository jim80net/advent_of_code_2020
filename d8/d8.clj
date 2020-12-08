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
        command (first ab)
        function (parse_function (last ab))]
    [command function]))

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
    (let [command (nth command_vec command_idx)
          action (first command)
          function (last command)]
      (if (visited? command_idx visited)
        acc
        (case action
          "nop" (recur (inc command_idx) (conj visited command_idx) acc)
          "jmp" (recur (function command_idx) (conj visited command_idx) acc)
          "acc" (recur (inc command_idx) (conj visited command_idx) (function acc)))))))

(deftest part_1_test (is (= 5 (part_1 parse_input_test_result_reference) )))

(def main
  (let [rules (parse_input input)
        p1_outcome   (part_1 rules)]
    (run-tests 'd8)
    (println (str "part 1 :" p1_outcome))))
