(ns leiningen.bouncer.fixes.namespace-sorting-test
  (:require [clojure.test :refer :all]
            [leiningen.bouncer.fixes.namespace-sorting :as sut]))


(deftest format-ns-test
  (is (= (sut/format-ns '(ns
                           leiningen.bouncer.fixes.namespace-sorting
                           (:require [clojure.java.io :as io]
                                     [clojure.string :as string])
                           (:import (java.io File))))
         "(ns leiningen.bouncer.fixes.namespace-sorting
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import (java.io File)))")))


(deftest sort-requires-test
  (is (= (sut/sort-requires '[[schema.core :as s]
                              [compojure.api.sweet :refer [GET POST]]
                              [ring.util.http-response :refer [content-type ok]]
                              [my-server.db.api.anomalies :as db-anomalies]
                              [my-server.web.handlers.schema.common :refer [ClickhouseFieldDef]]
                              [ring.swagger.json-schema :refer [describe]]])

         '[[compojure.api.sweet :refer [GET POST]]
           [my-server.db.api.anomalies :as db-anomalies]
           [my-server.web.handlers.schema.common :refer [ClickhouseFieldDef]]
           [ring.swagger.json-schema :refer [describe]]
           [ring.util.http-response :refer [content-type ok]]
           [schema.core :as s]])))


(deftest update-ns-test
  (is (= (sut/update-ns "(ns my-server
  (:require [my-server.web.handlers.schema.common :refer [ClickhouseFieldDef]]
            [compojure.api.sweet :refer [GET POST]]
            [ring.util.http-response :refer [content-type ok]]
            [ring.swagger.json-schema :refer [describe]]
            [my-server.db.api.anomalies :as db-anomalies]
            [schema.core :as s]))")

         "(ns my-server
  (:require [compojure.api.sweet :refer [GET POST]]
            [my-server.db.api.anomalies :as db-anomalies]
            [my-server.web.handlers.schema.common :refer [ClickhouseFieldDef]]
            [ring.swagger.json-schema :refer [describe]]
            [ring.util.http-response :refer [content-type ok]]
            [schema.core :as s]))"))

  ;; testing hints preservation
  (let [ns-with-hints "(ns ^:dev/once flow-constructor.app\n  (:require [flow-constructor.core :as core]\n            [cljs.spec.alpha :as s]\n            [expound.alpha :as expound]\n            [devtools.core :as devtools]))"
        ns-sorted     "(ns ^:dev/once flow-constructor.app\n  (:require [cljs.spec.alpha :as s]\n            [devtools.core :as devtools]\n            [expound.alpha :as expound]\n            [flow-constructor.core :as core]))"]
    (is (= (sut/update-ns ns-with-hints) ns-sorted))))


(deftest update-code-test
  (testing "update-code should sort the namespace block and leave the rest of the code untouched."
    (is (= (sut/update-code (slurp "test/leiningen/bouncer/data/namespaces/unsorted.cljtest"))
           (slurp "test/leiningen/bouncer/data/namespaces/sorted.cljtest")))
    (is (= (sut/update-code (slurp "test/leiningen/bouncer/data/namespaces/sorted.cljtest"))
           (slurp "test/leiningen/bouncer/data/namespaces/sorted.cljtest")))))
