(ns leiningen.bouncer.impl-test
  (:require [clojure.test :refer :all]
            [com.wallbrew.spoon.spec :as spoon.spec]
            [leiningen.bouncer.impl :as sut]
            [leiningen.bouncer.impl.io :as io]
            [leiningen.bouncer.types.config :as config]))


(deftest load-config!-test
  (testing "load-config! should return a valid config map."
    (is (map? (sut/load-config! {})))
    (is (spoon.spec/test-valid? ::config/config (sut/load-config! {}))))
  (testing "load-config! should return a valid config map if a config file isn't found."
    (with-redefs [io/file-exists? (constantly false)]
      (is (map? (sut/load-config! {})))
      (is (spoon.spec/test-valid? ::config/config (sut/load-config! {}))))))

