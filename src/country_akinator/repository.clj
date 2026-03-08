(ns country-akinator.repository
  (:require [clojure.java.jdbc :as jdbc]
            [country-akinator.db :as db]))

(defn load-all-countries []
  (jdbc/query (db/db-spec) ["SELECT * FROM countries ORDER BY name"]))

(defn load-country-by-id [id]
  (first
    (jdbc/query (db/db-spec)
                ["SELECT * FROM countries WHERE id = ?" id])))

(defn load-countries-by-id []
  (let [countries (load-all-countries)]
    (into {} (map (fn [country] [(:id country) country]) countries))))
