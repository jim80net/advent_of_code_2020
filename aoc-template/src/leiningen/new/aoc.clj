(ns leiningen.new.aoc
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "aoc"))

(defn aoc
  "Initialize a new Clojure project for use in Advent of Code"
  [name]
  (let [data {:name name
              :sanitized (name-to-path name)
              :day (clojure.string/join (rest name))}]
    (main/info "Generating fresh 'lein new' aoc project.")
    (->files data
             ["src/{{sanitized}}/foo.clj" (render "core.clj" data)]
             ["test/{{sanitized}}/core_test.clj" (render "core_test.clj" data)]
             ["README.md" (render "README.md" data)]
             ["input" ""]
             ["input.test" ""]
             ["input.test.result.edn" ""])))
