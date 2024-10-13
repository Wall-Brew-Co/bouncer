(ns leiningen.bouncer.fixes.api
  "The API for bouncer fixes"
  (:require [leiningen.core.main :as main]))

(defmulti fix!
  "Fix the project according to the rule.
   If the rule is unknown, a warning is printed.
   If the rule is disabled, a warning is printed.
   Otherwise, attempts to fix the project according to the rule."
  {:arglists '([project rule-key rule])}
  (fn [_project rule-key _rule]
    rule-key))


(defmethod fix! :default
  [_project rule-key _rule]
  (main/info (format "WARN: Unknown fix: %s" rule-key)))

(defn fix-all!
  "Perform all fixes against the project."
  [project project-rules]
  (letfn [(run-rule
            [acc rule-key rule]
            (assoc acc rule-key (fix! project rule-key rule)))]
    (reduce-kv run-rule {} project-rules)))
