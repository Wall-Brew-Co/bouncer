(ns leiningen.bouncer
  ;; The first line prints as the task description in `lein help`
  "Manage Leiningen plugins, the Wall Brew way.

   Main namespace for the bouncer plugin.
   Provides the entry point for leiningen and basic help functions."
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.api :as sealog]))

(defn bouncer
  "Manage Leiningen plugins, the Wall Brew way."
  [project & args]
  (let [command (first args)
        options (rest args)]
    (case command
      (main/warn "Unknown command: %s" command))))
