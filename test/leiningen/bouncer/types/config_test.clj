(ns leiningen.bouncer.types.config-test
  (:require [clojure.test :refer :all]
            [com.wallbrew.spoon.spec :as spoon.spec]
            [leiningen.bouncer.test-util :as test-util]
            [leiningen.bouncer.types.config :as sut]))


(deftest generatable?-test
  (testing "All specs can generate values that pass validation"
    (is (test-util/generatable? ::sut/config))
    (is (test-util/generatable? ::sut/project-rules))
    (is (test-util/generatable? ::sut/plugins))
    (is (test-util/generatable? ::sut/available-plugins))
    (is (test-util/generatable? ::sut/plugin))
    (is (test-util/generatable? ::sut/plugin-version))
    (is (test-util/generatable? ::sut/plugin-name))
    (is (test-util/generatable? ::sut/license))
    (is (test-util/generatable? ::sut/rule))
    (is (test-util/generatable? ::sut/comment))
    (is (test-util/generatable? ::sut/reason))
    (is (test-util/generatable? ::sut/disabled))))


(deftest valid-default-config-test
  (testing "The default config should be valid."
    (is (spoon.spec/test-valid? ::sut/config sut/default-config))))
