(ns country-akinator.db
  (:require [country-akinator.config :as config]))

(defn db-spec []
  (let [db (:db (config/load-config))]
    {:dbtype "mysql"
     :dbname (:name db)
     :host (:host db)
     :port (:port db)
     :user (:user db)
     :password (:password db)}))
