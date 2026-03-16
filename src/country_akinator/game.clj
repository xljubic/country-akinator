(ns country-akinator.game)

(defn matches-question? [country question]
  (let [kind (:kind question)
        attribute (:attribute question)
        value (:value question)
        country-value (get country attribute)]
    (cond
      (= kind :enum)
      (= country-value value)

      (= kind :boolean)
      (= country-value true)

      :else
      false)))

(defn apply-answer [countries question answer]
  (cond
    (= answer :yes)
    (filter (fn [country]
              (matches-question? country question))
            countries)

    (= answer :no)
    (filter (fn [country]
              (not (matches-question? country question)))
            countries)

    (= answer :dont-know)
    countries

    :else
    countries))

