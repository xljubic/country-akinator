(ns country-akinator.repository
  (:require [clojure.java.jdbc :as jdbc]
            [country-akinator.db :as db]))

(defn load-all-countries []
  (jdbc/query (db/db-spec) ["SELECT * FROM countries ORDER BY name"]))
