(ns country-akinator.scoring
  (:require [country-akinator.game :as game]))

(defn count-yes-for-question [countries question]
  (count (filter #(game/matches-question? % question) countries)))

(defn count-no-for-question [countries question]
  (- (count countries)
     (count-yes-for-question countries question)))

(defn question-score [countries question]
  (let [yes-count (count-yes-for-question countries question)
        no-count (count-no-for-question countries question)]
    (min yes-count no-count)))
