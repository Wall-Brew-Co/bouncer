(ns leiningen.bouncer.impl
  (:require [leiningen.bouncer.impl.io :as io]
            [leiningen.bouncer.types.config :as config]
            [leiningen.core.main :as main]))



;; Configuration functions

(defn bouncer-configured?
  "Returns true if the bouncer configuration file exists."
  []
  (io/file-exists? config/config-file))


(defn configure!
  "Create a new configuration file."
  [_opts]
  (io/create-file config/config-file)
  (io/write-edn-file! config/config-file config/default-config {:pretty-print-edn? true}))


(defn load-config!
  "Load the configuration file."
  []
  (if (io/file-exists? config/config-file)
    (io/read-edn-file! config/config-file ::config/config)
    (do (main/info "No configuration file found. Assuming default configuration.")
        config/default-config)))
