(ns country-akinator.core-test
  (:require [midje.sweet :refer :all]
            [country-akinator.config :as config]
            [country-akinator.db :as db]
            [country-akinator.repository :as repo]))

(fact "configuration can be loaded"
      (map? (config/load-config)) => true)

(fact "db-spec returns a mysql config map"
      (:dbtype (db/db-spec)) => "mysql")

(fact "countries can be loaded from repository"
      (count (repo/load-all-countries)) => pos?)

(fact "country can be loaded by id"
      (map? (repo/load-country-by-id 1)) => true)
