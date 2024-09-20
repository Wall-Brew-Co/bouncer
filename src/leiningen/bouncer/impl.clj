(ns leiningen.bouncer.impl
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as spec]
            [leiningen.bouncer.types.config :as config]
            [leiningen.core.main :as main]
            [spec-tools.core :as st]))


;; I/O functions

(defn file-exists?
  "Returns true if `path` points to a valid file"
  [path]
  (and (string? path)
       (.exists (io/file path))))


(defn read-file!
  "This is a wrapper around `slurp` that logs the filename to the console."
  [filename]
  (main/info (format "Reading from %s" filename))
  (slurp filename))


(defn read-edn-file!
  "Reads an EDN file and returns the contents as a map.
   Throws an exception if the file does not exist, or if the contents do not coerce"
  [filename spec]
  (if (file-exists? filename)
    (let [file-content (edn/read-string (read-file! filename))
          contents     (st/coerce spec file-content st/string-transformer)]
      (if (spec/valid? spec contents)
        contents
        (throw (ex-info (str "Invalid file contents: " filename)
                        {:filename filename
                         :errors   (spec/explain-str spec contents)}))))
    (throw (ex-info "Not matching file exists!"
                    {:filename filename}))))


(defn write-file!
  "This is a wrapper around `spit` that logs the filename to the console."
  [filename content]
  (main/info (format "Writing to %s" filename))
  (spit filename content))


(defn write-edn-file!
  "Write the contents to a file as EDN."
  [filename content {:keys [pretty-print-edn?]}]
  (if pretty-print-edn?
    (write-file! filename (with-out-str (pp/pprint content)))
    (write-file! filename content)))


;; Configuration functions

(defn bouncer-configured?
  "Returns true if the bouncer configuration file exists."
  []
  (file-exists? config/config-file))


(defn configure!
  "Create a new configuration file."
  [_opts]
  (io/make-parents (io/file config/config-file))
  (write-edn-file! config/config-file config/default-config {:pretty-print-edn? true}))


(defn load-config!
  "Load the configuration file."
  []
  (if (file-exists? config/config-file)
    (read-edn-file! config/config-file ::config/config)
    (do (main/info "No configuration file found. Assuming default configuration.")
        config/default-config)))
