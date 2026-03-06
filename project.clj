(defproject country-akinator "0.1.0-SNAPSHOT"
  :description "CLI Akinator-like game for guessing UN member countries"
  :url "https://github.com/xljubic/country-akinator"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.2"]
                 [org.clojure/data.csv "1.1.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "8.0.33"]
                 [midje "1.10.10"]]
  :plugins [[lein-midje "3.2.2"]]
  :main ^:skip-aot country-akinator.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
