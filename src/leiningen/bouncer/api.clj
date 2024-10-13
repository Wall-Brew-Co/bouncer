(ns leiningen.bouncer.api
  "The public API for bouncer.

   This namespace contains the public functions that are called by the leiningen"
  (:require [leiningen.bouncer.fixes.api :as fixes]
            [leiningen.bouncer.fixes.namespace-sorting]
            [leiningen.bouncer.impl :as impl]
            [leiningen.bouncer.project-rules.api :as project-rules]
            [leiningen.bouncer.project-rules.license]
            [leiningen.bouncer.project-rules.plugins]
            [leiningen.core.main :as main]))


(defn init
  "Create a new configuration file if one does not exist."
  [project _options]
  (let [configuration (impl/load-config! project)]
    (if (impl/bouncer-configured? project)
      (main/info "Existing Bouncer configuration found.")
      (impl/configure! configuration))))


(defn check
  "Check the project.clj file against Wall Brew standards."
  [project _options]
  (let [{:keys [project-rules]} (impl/load-config! project)]
    (main/info "Checking project.clj against Wall Brew standards...")
    (let [project-results (project-rules/check-all project project-rules)
          results         (vals project-results)]
      (if (every? true? results)
        (do (main/info "PASS: Project conforms to all rules.")
            (System/exit 0))
        (do
          (main/info "FAIL: Project does not conform to one or more rules.")
          (System/exit 1))))))

(defn fix
  "Automatically fix common issues and style violations."
  [project _options]
  (let [{:keys [fixes]} (impl/load-config! project)]
    (main/info "Fixing project.clj against Wall Brew standards...")
    (let [_applied-fixes (fixes/fix-all! project fixes)]
      (main/info "Project has been fixed.")
      (System/exit 0))))
