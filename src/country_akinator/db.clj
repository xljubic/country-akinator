(ns country-akinator.db
  (:require [country-akinator.config :as config]
            [clojure.java.jdbc :as jdbc]))

(defn db-spec []
  (let [db (:db (config/load-config))]
    {:dbtype "mysql"
     :dbname (:name db)
     :host (:host db)
     :port (:port db)
     :user (:user db)
     :password (:password db)}))

(defn test-connection []
  (jdbc/query (db-spec) ["SELECT 1 AS result"]))


