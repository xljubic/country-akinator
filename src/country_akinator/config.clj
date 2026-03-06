(ns country-akinator.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn load-config []
  (let [file (io/resource "config.edn")
        text (slurp file)
        config (edn/read-string text)]
    config))