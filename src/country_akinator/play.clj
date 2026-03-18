(ns country-akinator.play
  (:require [country-akinator.questions :as questions]
            [country-akinator.game :as game]
            [country-akinator.scoring :as scoring]))

(defn remaining-questions [countries asked-questions]
  (remove (set asked-questions)
          (questions/generate-country-questions countries)))

(defn select-next-question [countries asked-questions]
  (let [questions (remaining-questions countries asked-questions)]
    (scoring/choose-next-question countries questions)))

(defn read-answer []
  (println "Type: yes / no / dont-know")
  (let [input (read-line)]
    (cond
      (= input "yes") :yes
      (= input "no") :no
      (= input "dont-know") :dont-know
      :else nil)))

(defn game-finished? [countries]
  (or (<= (count countries) 1)
      (empty? countries)))

(defn play-round [countries asked-questions]
  (if (game-finished? countries)
    countries
    (let [question (select-next-question countries asked-questions)]
      (if (nil? question)
        countries
        (do
          (println)
          (println (questions/question-text question))
          (let [answer (read-answer)]
            (if (nil? answer)
              (do
                (println "Invalid input. Please try again.")
                (play-round countries asked-questions))
              (play-round
                (vec (game/apply-answer countries question answer))
                (conj asked-questions question)))))))))

(defn print-result [countries]
  (println)
  (cond
    (empty? countries)
    (println "No country matches the given answers.")

    (= 1 (count countries))
    (println "I think your country is:" (:name (first countries)))

    :else
    (do
      (println "My best guesses are:")
      (doseq [country countries]
        (println "-" (:name country))))))

(defn play-game [countries]
  (println "Think of a UN member country.")
  (println "I will try to guess it.")
  (let [result (play-round countries [])]
    (print-result result)))