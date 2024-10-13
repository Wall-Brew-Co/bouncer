(ns leiningen.bouncer.types.config
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [spec-tools.core :as st]))


(def ^:const config-file
  "The default name of the configuration file."
  ".bouncer/config.edn")


(def ^:const backup-config-file
  "The secondary name of the configuration file."
  ".wallbrew/bouncer/config.edn")


(spec/def ::disabled
  (st/spec
    {:type        :boolean
     :spec        boolean?
     :description "Whether the rule is disabled."}))


(spec/def ::reason
  (st/spec
    {:type        :string
     :spec        (spec/and string? #(not (str/blank? %)))
     :description (str "The reason the rule is disabled. "
                       "The string must not be empty or blank.")}))


(spec/def ::comment
  (st/spec
    {:type        :string
     :spec        (spec/and string? #(not (str/blank? %)))
     :description "An informative comment."}))


(spec/def ::rule
  (st/spec
    {:type        :map
     :spec        (spec/and
                    (spec/keys :opt-un [::comment
                                        ::disabled
                                        ::reason])
                    #(or (not (true? (:disabled %)))
                         (and (true? (:disabled %))
                              (string? (:reason %)))))
     :description (str "A rule that bouncer can enforce. "
                       "Each rule can be disabled, but is enabled by default. "
                       "If the rule is disabled, a reason must be recorded.")}))


(spec/def ::license
  (st/spec
    {:type        :map
     :spec        ::rule
     :description (str "The license information for the project. "
                       "If the rule is disabled, a reason must be recorded. "
                       "For example, \"Inherited Eclipse License from source of fork\"")}))


(spec/def ::plugin-name
  (st/spec
    {:type        :string
     :spec        (spec/and string? #(not (str/blank? %)))
     :description "The name of the plugin to install."}))


(spec/def ::plugin-version
  (st/spec
    {:type        :string
     :spec        (spec/and string? #(not (str/blank? %)))
     :description (str "The version of the plugin to install if it is not already installed. "
                       "Wall Brew projects should use Renovate to manage dependency versions.")}))


(spec/def ::plugin
  (st/spec
    {:type        :map
     :spec        (spec/and
                    (spec/keys :req-un [::plugin-name
                                        ::plugin-version]
                               :opt-un [::comment
                                        ::disabled
                                        ::reason])
                    #(or (not (true? (:disabled %)))
                         (and (true? (:disabled %))
                              (string? (:reason %)))))
     :description (str "A rule that bouncer can enforce. "
                       "Each rule can be disabled, but is enabled by default. "
                       "If the rule is disabled, a reason must be recorded.")}))


(spec/def ::available-plugins
  (st/spec
    {:type        :vector
     :spec        (spec/coll-of ::plugin :into [] :kind vector?)
     :description "The plugins required to run Wall Brew CI."}))


(spec/def ::plugins
  (st/spec
    {:type        :map
     :spec        (spec/and ::rule
                            (spec/keys :opt-un [::available-plugins]))
     :description (str "The plugins required to run Wall Brew CI. "
                       "If the rule is disabled, a reason must be recorded. "
                       "For example, \"This repository is a fork which uses an alternative formatter.\"")}))


(spec/def ::project-rules
  (st/spec
    {:type        :map
     :spec        (spec/keys :opt-un [::comment
                                      ::license
                                      ::plugins])
     :description "The rules against project.clj that bouncer can enforce and their configuration."}))


(spec/def ::config
  (st/spec
    {:type        :map
     :spec        (spec/keys :opt-un [::comment
                                      ::project-rules])
     :description "The configuration for Bouncer."}))


(def default-config
  "The default configuration for Bouncer."
  {:comment       "The default configuration for Wall Brew projects."
   :project-rules {:license {:comment  "https://github.com/Wall-Brew-Co/open-source?tab=readme-ov-file#licensing"
                             :disabled false}
                   :plugins {:disabled          false
                             :available-plugins [{:comment        "https://github.com/Wall-Brew-Co/rebroadcast?tab=readme-ov-file#sealog-configuration"
                                                  :plugin-name    "com.wallbrew/lein-sealog"
                                                  :plugin-version "1.6.0"}
                                                 {:comment        "https://github.com/Wall-Brew-Co/rebroadcast?tab=readme-ov-file#clj-kondo-configuration"
                                                  :plugin-name    "com.github.clj-kondo/lein-clj-kondo"
                                                  :plugin-version "2024.08.29"}
                                                 {:plugin-name    "lein-cljsbuild"
                                                  :plugin-version "1.1.8"
                                                  :disabled       true
                                                  :reason         "cljsbuild is only required for ClojureScript and cross-platform projects."}
                                                 {:comment        "https://github.com/Wall-Brew-Co/rebroadcast?tab=readme-ov-file#cljstyle-configuration"
                                                  :plugin-name    "mvxcvi/cljstyle"
                                                  :plugin-version "0.16.630"}]}}})
