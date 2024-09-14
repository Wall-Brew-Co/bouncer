(ns leiningen.bouncer.project-rules.api
  "The API for bouncer rules."
  (:require [leiningen.core.main :as main]))


(defmulti check
  "Check if the project conforms to the configured rule.
   If the rule is unknown, a warning is printed.
   If the rule is disabled, a warning is printed.
   Otherwise, returns true if the project conforms to the rule and false otherwise."
  {:arglists '([project rule-key rule])}
  (fn [_project rule-key _rule]
    rule-key))


(defmethod check :default
  [_project rule-key _rule]
  (main/info (format "WARN: Unknown rule: %s" rule-key)))

(defn check-all
  [project project-rules]
  (letfn [(run-rule [acc rule-key rule]
            (assoc acc rule-key (check project rule-key rule)))]
    (reduce-kv run-rule {} project-rules)))


(defn record-failure
  "Log a failure message to STDERR and return false."
  [message]
  (main/info (format "FAIL: %s" message))
  false)

