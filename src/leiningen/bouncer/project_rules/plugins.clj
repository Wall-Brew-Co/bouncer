(ns leiningen.bouncer.project-rules.plugins
  (:require [clojure.string :as str]
            [leiningen.bouncer.project-rules.api :as api]
            [leiningen.core.main :as main]))


(defn plugin-installed?
  "Check if a plugin is installed."
  [project-plugins {:keys [plugin-name]}]
  (some #(= (str (first %)) plugin-name) project-plugins))


(defn plugins->errors
  "Validate the plugins information in the project."
  [project-plugins {:keys [available-plugins]}]
  (let [disabled-plugin-rules (filter :disabled available-plugins)
        enabled-plugin-rules (remove :disabled available-plugins)]
    (when (seq disabled-plugin-rules)
      (main/info "WARN: Some plugins rules are disabled. Skipping..."))
    (reduce (fn [acc rule]
              (if (plugin-installed? project-plugins rule)
                acc
                (assoc acc (:plugin-name rule) "Plugin not installed.")))
            {}
            enabled-plugin-rules)))


(defn check*
  "Check the plugins information in the project."
  [{:keys [plugins]} rule]
  (let [errors (plugins->errors plugins rule)]
    (cond
      (:disabled rule)
      (do
        (main/info "WARN: plugins rule is disabled. Skipping...")
        true)

      (empty? errors)
      (do
        (main/info "PASS: Plugins information is valid.")
        true)

      :else
      (let [missing-plugins (->> errors
                                 keys
                                 (map #(str "'" % "'"))
                                 (str/join ", "))]
        (api/record-failure (str "One or plugins not installed: " missing-plugins))))))


(defmethod api/check :plugins
  [project _rule-key rule]
  (check* project rule))
