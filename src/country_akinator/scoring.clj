(ns country-akinator.scoring
  (:require [country-akinator.game :as game]))

(defn count-yes-for-question [countries question]
  (count
    (filter
      (fn [country]
        (game/matches-question? country question))
      countries)))

(defn count-no-for-question [countries question]
  (- (count countries)
     (count-yes-for-question countries question)))

(defn question-score [countries question]
  (let [yes-count (count-yes-for-question countries question)
        no-count (count-no-for-question countries question)]
    (min yes-count no-count)))

(defn score-question [countries question]
  {:question question
   :score (question-score countries question)})

(defn rank-questions [countries questions]
  (sort-by
    :score
    >
    (map #(score-question countries %) questions)))

(defn top-questions [countries questions n]
  (take n
        (rank-questions countries questions)))

(defn choose-next-question [countries questions]
  (let [top5 (vec (top-questions countries questions 5))]
    (when (seq top5)
      (:question (rand-nth top5)))))