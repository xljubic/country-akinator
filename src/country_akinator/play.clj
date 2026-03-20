(ns country-akinator.play
  (:require [clojure.string :as str]
            [country-akinator.questions :as questions]
            [country-akinator.game :as game]
            [country-akinator.scoring :as scoring]))

(defn remaining-questions [countries asked-questions]
  (remove (set asked-questions)
          (questions/generate-country-questions countries)))

(defn select-next-question [countries asked-questions]
  (let [regular-questions (remaining-questions countries asked-questions)
        positive-regular-questions (scoring/top-questions countries regular-questions 5)
        fallback-question (when (< (count positive-regular-questions) 5)
                            (questions/random-fallback-question countries))
        all-questions (cond-> (vec regular-questions)
                              fallback-question (conj fallback-question))
        available-questions (remove (set asked-questions) all-questions)]
    (scoring/choose-next-question countries available-questions)))

(defn read-answer []
  (println "Type: yes / no / dont-know")
  (let [input (read-line)]
    (cond
      (= input "yes") :yes
      (= input "no") :no
      (= input "dont-know") :dont-know
      :else nil)))

(defn game-finished? [countries]
  (or (= (count countries) 1)
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

(defn format-number [n]
  (format "%,d" (long n)))

(defn format-text-value [value]
  (if (string? value)
    (str/replace value "_" " ")
    value))

(defn join-values [values]
  (str/join ", " (sort values)))

(defn print-country-summary [country]
  (let [name (:name country)
        continent (format-text-value (:continent country))
        population (format-number (:population country))
        area (format-number (:area country))
        bordering-countries (:number_of_bordering_countries country)
        religion (format-text-value (:religion_majority country))
        language-family (format-text-value (:main_language_family country))
        organizations (:organizations country)
        regions (:regions country)]
    (println (str name " is a country in " continent "."))
    (println (str "Population: " population))
    (println (str "Area: " area " km²"))
    (println (str "Number of bordering countries: " bordering-countries))
    (println (str "Major religion: " religion))
    (println (str "Main language family: " language-family))
    (when (seq organizations)
      (println (str "Member of organizations: " (join-values organizations))))
    (when (seq regions)
      (println (str "Located in regions: " (join-values regions))))))

(defn print-result [countries]
  (println)
  (cond
    (empty? countries)
    (println "No country matches the given answers.")

    (= 1 (count countries))
    (let [country (first countries)]
      (println (str "I think your country is: " (:name country)))
      (println)
      (print-country-summary country))

    :else
    (do
      (println "I could not narrow it down to one country.")
      (println "Is your country one of these?")
      (doseq [country countries]
        (println "-" (:name country))))))

(defn read-play-again []
  (println)
  (println "Do you want to play another round? yes / no")
  (let [input (read-line)]
    (cond
      (= input "yes") true
      (= input "no") false
      :else
      (do
        (println "Invalid input. Please type yes or no.")
        (read-play-again)))))

(defn play-game [countries]
  (loop []
    (println)
    (println "Think of a UN member country.")
    (println "I will try to guess it.")
    (let [result (play-round countries [])]
      (print-result result)
      (when (read-play-again)
        (recur)))))