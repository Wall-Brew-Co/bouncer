(ns leiningen.bouncer
  ;; The first line prints as the task description in `lein help`
  "Manage Leiningen plugins, the Wall Brew way.

   Main namespace for the bouncer plugin.
   Provides the entry point for leiningen and basic help functions."
  (:require [leiningen.bouncer.api :as bouncer]
            [leiningen.core.main :as main]))


(defn unknown-command
  "Formats an error message for an unknown command."
  [command]
  (str "Unknown command: " command "\nAvailable commands: `init`, `check`, and `help`"))


(defn top-level-help
  "Display help text for bouncer in general"
  []
  (main/info "Bouncer - A leiningen configuration manager for Wall Brew Projects.")
  (main/info "")
  (main/info "Usage: lein bouncer <command> [options]")
  (main/info "")
  (main/info "Available commands:")
  (main/info "  init    - Creates a default configuration file.")
  (main/info "  check   - Verify the project.clj file against Wall Brew standards.")
  (main/info "  help    - Display this help message.")
  (main/info "")
  (main/info "Run `lein bouncer help <command>` for more information on a specific command."))


(defn init-help
  "Display help text for the init command."
  []
  (main/info "Usage: lein bouncer init")
  (main/info "")
  (main/info "Creates a default configuration file.")
  (main/info "If a configuration file already exists, this command will do nothing."))


(defn check-help
  "Display help text for the check command"
  []
  (main/info "Usage: lein bouncer check")
  (main/info "")
  (main/info "Verify the project.clj file against Wall Brew standards.")
  (main/info "Will exit abnormally if any issues are found.")
  (main/info "Checks can be disabled and modified in the configuration file.")
  (main/info "")
  (main/info "Available Checks:")
  (main/info "  - Validates the OSS license.")
  (main/info "  - Verifies the plugins required to run Wall Brew CI."))


(defn help
  "Display help text for a specific command."
  [options]
  (let [command (first options)]
    (case command
      nil        (top-level-help)
      "init"     (init-help)
      "check"    (check-help)
      "help"     (main/info "Run `lein bouncer help <command>` for more information on a specific command.")
      (main/info (unknown-command command)))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}


(defn bouncer
  "Manage Leiningen plugins, the Wall Brew way."
  [project & args]
  (let [command (first args)
        options (rest args)]
    (case command
      "init"    (bouncer/init options)
      "check"   (bouncer/check project)
      "help"    (help options)
      (unknown-command command))))
