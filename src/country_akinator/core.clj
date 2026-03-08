(ns country-akinator.core
  (:gen-class)
  (:require [country-akinator.repository :as repo]
            [country-akinator.db :as db]))

(defn -main [& args]
  (println "Country Akinator started.")
  (println)

  (println "Testing database connection...")
  (println (db/test-connection))
  (println)

  (println "Loading countries from database...")
  (println "Number of countries:" (count (repo/load-all-countries)))
  (println)

  (println "First 3 countries:")
  (doseq [country (take 3 (repo/load-all-countries))]
    (println country))
  (println)

  (println "Database read successful."))