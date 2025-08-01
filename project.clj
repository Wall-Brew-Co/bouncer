(defproject com.wallbrew/bouncer "1.2.0"
  :description "A Leiningen plugin to manage Leiningen."
  :url "https://github.com/Wall-Brew-Co/bouncer"
  :license {:name         "MIT"
            :url          "https://opensource.org/licenses/MIT"
            :distribution :repo
            :comments     "Same-as all Wall-Brew projects"}
  :scm {:name "git"
        :url  "https://github.com/Wall-Brew-Co/bouncer"}
  :dependencies [[org.clojure/clojure "1.12.1"]
                 [metosin/spec-tools "0.10.7"]]
  :plugins [[com.github.clj-kondo/lein-clj-kondo "2025.07.28"]
            [com.wallbrew/lein-sealog "1.9.0"]
            [com.wallbrew/bouncer "1.2.0"]
            [mvxcvi/cljstyle "0.17.642"]]
  :pom-addition [:organization
                 [:name "Wall Brew Co."]
                 [:url "https://wallbrew.com"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.1"]]}}
  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_user
                                    :password      :env/clojars_pass
                                    :sign-releases false}]]
  :deploy-branches ["master"]
  :global-vars {*warn-on-reflection* true}
  :eval-in-leiningen true)
