(ns country-akinator.core
  (:gen-class)
  (:require [country-akinator.config :as config]
            [country-akinator.db :as db]))

(defn -main [& args]
  (println "Country Akinator started.")
  (println)

  (println "Loading configuration...")
  (println (config/load-config))
  (println)

  (println "Preparing database connection...")
  (println (db/db-spec))
  (println)

  (println "Setup successful."))