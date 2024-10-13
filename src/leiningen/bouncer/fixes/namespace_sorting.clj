(ns leiningen.bouncer.fixes.namespace-sorting
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [leiningen.bouncer.fixes.api :as api]
            [leiningen.bouncer.impl.io :as io]
            [leiningen.core.main :as main])
  (:import (java.io File)))


(defn format-ns
  "Format ns block into a writable string."
  [data]
  (let [sb (StringBuilder. (str "(ns " (second data)))]
    (doseq [item (drop 2 data)]
      (cond
        (= :require (first item)) (do (.append sb \newline)
                                      (.append sb "  (:require ")
                                      (.append sb (second item))
                                      (doseq [req (drop 2 item)]
                                        (.append sb \newline)
                                        (.append sb "            ")
                                        (.append sb req))
                                      (.append sb ")"))

        (= :import (first item)) (do (.append sb \newline)
                                     (.append sb "  (:import ")
                                     (.append sb (second item))
                                     (doseq [req (drop 2 item)]
                                       (.append sb \newline)
                                       (.append sb "           ")
                                       (.append sb req))
                                     (.append sb ")"))
        (string? item) (do (.append sb \newline)
                           (.append sb "  ")
                           (.append sb
                                    (str "\"" (str/escape item {\" "\\\""}) "\"")))
        :else (do (.append sb \newline)
                  (.append sb "  ")
                  (.append sb (str item)))))
    (.append sb ")")
    (.toString sb)))


(defn sort-fn
  "Convert a `:require` entry into a string for sorting."
  [form]
  (if (sequential? form)
    (-> form first str)
    (str form)))


(defn sort-requires
  "Sort the namespace `:requires` list.
   Unlike, the ns-sort leiningen plugin, no priority is given to project namespaces."
  [requires]
  (distinct (sort-by sort-fn requires)))


(defn make-replacement-table
  "Sometimes there can be some hints in the namespace definition.

   e.g.:
   ```clj
   (ns ^:dev/once my-app.core
     ...`
   ```
   So we need to save such `^:dev/once` hints.
   Constructing replacement table:

   {#`flow-constructor.app`  =>  `^:dev/once flow-constructor.app`
   ...}"
  [^String namespace-block]
  (let [table (->> namespace-block
                   (re-seq #"(\^:[^\s]+)\s+([^\s]+)")
                   (map (fn [[full _ replacement]]
                          [(re-pattern replacement) full]))
                   (into {}))]
    table))


(defn replace-with-table
  "Apply replacement table to string"
  [^String require-string table]
  (letfn [(replace-string
            [original-string [pattern replacement]]
            (str/replace original-string pattern replacement))]
    (reduce replace-string require-string table)))


(defn update-ns
  "Parse ns string block and update ns block."
  [^String ns-block-as-string]
  (let [replace-table   (make-replacement-table ns-block-as-string)
        data            (edn/read-string ns-block-as-string)
        requires        (first (filter #(and (sequential? %)
                                             (= :require (first %)))
                                       data))
        requires-sorted (concat [:require]
                                (sort-requires (rest requires)))
        sorted-data     (map
                          (fn [item]
                            (if (and (sequential? item)
                                     (= :require (first item)))
                              requires-sorted
                              item))
                          data)]

    ;; if the order is the same, keep old code format
    (if-not (= data sorted-data)
      (replace-with-table (format-ns sorted-data) replace-table)
      ns-block-as-string)))


(defn update-code
  "Read the code string and update the namespace declaration to sort the required namespaces."
  [^String code]
  (let [ns-start (.indexOf code "(ns")
        ns-end   (loop [start ns-start position 0]
                   (cond
                     (= \( (.charAt code start)) (recur (inc start) (inc position))
                     (= \) (.charAt code start)) (if (zero? (dec position))
                                                   (inc start)
                                                   (recur (inc start) (dec position)))
                     :else (recur (inc start) position)))
        ns-data  (subs code ns-start ns-end)
        prefix   (subs code 0 ns-start)
        postfix  (subs code ns-end)]
    (if (str/includes? ns-data ";")
      code
      (str prefix (update-ns ns-data) postfix))))


(defn sort-file
  "Read, update and write to file"
  [^File file]
  (main/info (format "Sorting file: %s" (io/file->path file)))
  (try
    (let [data (slurp file)]
      (spit file (update-code data)))
    (catch Exception e
      (main/warn (format "Cannot update file: %s" (io/file->path file)) e))))


(defn sort-path
  "Filter for only .clj, .cljs, .cljc files"
  [path]
  (let [clojure-files (io/->clojure-source-files (io/list-files-under-path path))]
    (doseq [file clojure-files]
      (sort-file file))))


(defn fix!*
  "Fix the sorting of namespace `:require` blocks."
  [project rule]
  (if (:disabled rule)
    (do (main/info "WARN: Namespace sorting rule is disabled. Skipping...")
        true)
    (do (main/info "Sorting namespace `:require` blocks.")
        (let [code-paths (concat (:source-paths project)
                                 (:test-paths project))]
          (doseq [path code-paths]
            (sort-path path))))))


(defmethod api/fix! :namespace-sorting
  [project _rule-key rule]
  (fix!* project rule))
