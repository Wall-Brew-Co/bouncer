(ns leiningen.bouncer.impl-test
  (:require [clojure.test :refer :all]
            [com.wallbrew.spoon.spec :as spoon.spec]
            [leiningen.bouncer.impl :as sut]
            [leiningen.bouncer.impl.io :as io]
            [leiningen.bouncer.types.config :as config]))


(deftest deep-merge-test
  (testing "Deep merge behaves the same as merge for 1-level maps."
    (is (= {:a 1 :b 2}
           (sut/deep-merge {:a 1} {:b 2})
           (merge {:a 1} {:b 2})))
    (is (= {:a 2}
           (sut/deep-merge {:a 1} {:a 2})
           (merge {:a 1} {:a 2})))
    (is (= {:a 2}
           (sut/deep-merge {:a 2} {})
           (merge {:a 2} {}))))
  (testing "Deep merge recursively merges maps, preferring maps with content."
    (is (= {:a {:b 2}}
           (sut/deep-merge {:a {:b 1}} {:a {:b 2}})))
    (is (= {:a {:b 1}}
           (sut/deep-merge {:a {:b 1}} {:a {}})))
    (is (= {:a {:b [1 2 3]}}
           (sut/deep-merge {:a {:b [4 5 6]}}
                           {:a {:b [1 2 3]}})))
    (is (= {:a {:b true :c false :d nil}}
           (sut/deep-merge {:a {:b true :c true}}
                           {:a {:c false :d nil}})))))


(deftest load-config!-test
  (testing "load-config! should return a valid config map."
    (is (map? (sut/load-config! {})))
    (is (spoon.spec/test-valid? ::config/config (sut/load-config! {}))))
  (testing "load-config! should return a valid config map if a config file isn't found."
    (with-redefs [io/file-exists? (constantly false)]
      (is (map? (sut/load-config! {})))
      (is (spoon.spec/test-valid? ::config/config (sut/load-config! {}))))))

