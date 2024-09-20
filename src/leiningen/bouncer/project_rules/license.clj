(ns leiningen.bouncer.project-rules.license
  (:require [leiningen.bouncer.project-rules.api :as api]
            [leiningen.core.main :as main]))


(def mit-license
  "The MIT License block used by Wall Brew projects."
  {:name         "MIT"
   :url          "https://opensource.org/licenses/MIT"
   :distribution :repo
   :comments     "Same-as all Wall-Brew projects"})


(defn license->errors
  "Validate the license information in the project."
  [license]
  (let [has-license?           (boolean license)
        matching-name?         (= (:name license)
                                  (:name mit-license))
        matching-url?          (= (:url license)
                                  (:url mit-license))
        matching-distribution? (= (:distribution license)
                                  (:distribution mit-license))
        matching-comments?     (= (:comments license)
                                  (:comments mit-license))
        errors                 (cond-> {}
                                 (not matching-name?) (assoc :name "Name does not match. ")
                                 (not matching-url?) (assoc :url "URL does not match. ")
                                 (not matching-distribution?) (assoc :distribution "Distribution does not match. ")
                                 (not matching-comments?) (assoc :comments "Comments do not match. "))]
    (if has-license?
      errors
      {:missing "License information not found."})))


(defn check*
  "Check the license information in the project."
  [{:keys [license]} rule]
  (let [errors (license->errors license)]
    (cond
      (:disabled rule)
      (do
        (main/info "WARN: License rule is disabled. Skipping...")
        true)

      (empty? errors)
      (do
        (main/info "PASS: License information is valid.")
        true)

      :else
      (api/record-failure (str "License information failed validation: " (vals errors))))))


(defmethod api/check :license
  [project _rule-key rule]
  (check* project rule))
