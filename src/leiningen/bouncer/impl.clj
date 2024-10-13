(ns leiningen.bouncer.impl
  (:require [clojure.spec.alpha :as spec]
            [leiningen.bouncer.impl.io :as io]
            [leiningen.bouncer.types.config :as config]
            [leiningen.core.main :as main]))


;; Configuration functions

(defn bouncer-configured?
  "Returns true if the sealog configuration exists either in project.clj or in a configuration file."
  [project]
  (or (:bouncer project)
      (io/file-exists? config/config-file)
      (io/file-exists? config/backup-config-file)))


(defn configure!
  "Create a new configuration file."
  [_opts]
  (io/create-file config/config-file)
  (io/write-edn-file! config/config-file config/default-config {:pretty-print-edn? true}))


(defn select-config
  "Select the configuration to use with the following precedence:
    - The `:bouncer` key in project.clj
    - The configuration file in .bouncer/config.edn
    - The configuration file in .wallbrew/bouncer/config.edn
    - The default configuration"
  [project]
  (let [project-config             (:bouncer project)
        config-file-exists?        (io/file-exists? config/config-file)
        backup-config-file-exists? (io/file-exists? config/backup-config-file)]
    (cond
      (map? project-config)      project-config
      config-file-exists?        (io/read-edn-file! config/config-file ::config/config)
      backup-config-file-exists? (io/read-edn-file! config/backup-config-file ::config/config)
      :else                      (do (main/info "No configuration file found. Assuming default configuration.")
                                     config/default-config))))


(defn deep-merge
  "Recursively merge two maps."
  [m1 m2]
  (if (and (map? m1) (map? m2))
    (reduce (fn [acc [k v]]
              (assoc acc k (deep-merge (get m1 k) v)))
            m1
            m2)
    m2))


(defn load-config!
  "Load the configuration file with the following precedence:
      - The `:bouncer` key in project.clj
      - The configuration file in .bouncer/config.edn
      - The configuration file in .wallbrew/bouncer/config.edn
      - The default configuration
     If the configuration is invalid, print a warning and exit.

   N.B - This function merges the configuration file with the default configuration."
  [project]
  (let [config               (select-config project)
        config-with-defaults (deep-merge config/default-config config)]
    (if (spec/valid? ::config/config config-with-defaults)
      config-with-defaults
      (do (main/warn (format "Invalid configuration file contents: %s" (spec/explain-str ::config/config config-with-defaults)))
          (main/exit 1)))))
