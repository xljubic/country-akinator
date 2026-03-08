(ns country-akinator.core-test
  (:require [midje.sweet :refer :all]
            [country-akinator.config :as config]
            [country-akinator.db :as db]))

(fact "configuration can be loaded"
      (map? (config/load-config)) => true)

(fact "db-spec returns a mysql config map"
      (:dbtype (db/db-spec)) => "mysql")
