(ns leiningen.bouncer.api
  "The public API for bouncer.

   This namespace contains the public functions that are called by the leiningen"
  (:require [leiningen.bouncer.impl :as impl]
            [leiningen.bouncer.rules.api :as rules]
            [leiningen.bouncer.rules.license]
            [leiningen.bouncer.rules.plugins]
            [leiningen.core.main :as main]))


(defn init
  "Create a new configuration file if one does not exist.."
  [_opts]
  (let [configuration (impl/load-config!)]
    (if (impl/bouncer-configured?)
      (main/info "Existing Bouncer configuration found.")
      (impl/configure! configuration))))

(defn check
  "Check the project.clj file against Wall Brew standards."
  [project]
  (let [{:keys [project-rules]} (impl/load-config!)]
    (main/info "Checking project.clj against Wall Brew standards...")
    (let [results (rules/check-all project project-rules)]
      (if (every? true? (vals results))
        (do (main/info "PASS: Project conforms to all rules.")
            (System/exit 0))
        (do
          (main/info "FAIL: Project does not conform to one or more rules." )
          (System/exit 1))))))
