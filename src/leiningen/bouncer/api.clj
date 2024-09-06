(ns leiningen.bouncer.api
  "The public API for bouncer.

   This namespace contains the public functions that are called by the leiningen"
  (:require [leiningen.bouncer.impl :as impl]
            [leiningen.core.main :as main]))


(defn init
  "Create a new configuration file if one does not exist.."
  [_opts]
  (let [configuration (impl/load-config!)]
    (if (impl/bouncer-configured?)
      (main/info "Existing Bouncer configuration found.")
      (impl/configure! configuration))))
