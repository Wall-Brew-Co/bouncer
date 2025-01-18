(ns leiningen.bouncer.impl.io
  "This namespace contains functions for reading and writing files."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [leiningen.core.main :as main]
            [spec-tools.core :as st])
  (:import (java.io File)))


(def create-file
  "Create a file with all of its parent directories."
  (comp io/make-parents io/file))


(defn file-exists?
  "Returns true if `path` points to a valid file"
  [path]
  (and (string? path)
       (.exists (io/file path))))


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
                         :errors   (spec/explain-data spec contents)}))))
    (throw (ex-info "Not matching file exists!"
                    {:filename filename}))))


(defn list-files-under-path
  "List all files under a given path."
  [path]
  (file-seq (io/file path)))


(defn ->clojure-source-files
  "Filter `files` for only .clj, .cljs, .cljc files."
  [files]
  (letfn [(clojure-source-file?
            [^File file]
            (or (str/ends-with? (.getAbsolutePath file) ".clj")
                (str/ends-with? (.getAbsolutePath file) ".cljs")
                (str/ends-with? (.getAbsolutePath file) ".cljc")))]
    (filter clojure-source-file? files)))


(defn file->path
  "Convert a file to a path."
  [^File file]
  (.getAbsolutePath file))
