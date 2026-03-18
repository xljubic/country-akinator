(ns country-akinator.core
  (:gen-class)
  (:require [country-akinator.repository :as repository]
            [country-akinator.play :as play]))

(defn -main [& args]
  (let [countries (repository/load-all-countries)]
    (play/play-game countries)))