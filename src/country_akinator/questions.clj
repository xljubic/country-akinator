(ns country-akinator.questions
  (:require [clojure.string :as str]))

(def religion-question-values
  #{"christianity" "islam" "hinduism" "buddhism" "judaism" "folk_religion"})

(def enum-question-definitions
  [{:attribute :continent
    :text-fn (fn [value]
               (str "Is your country in " (str/capitalize value) "?"))}
   {:attribute :religion_majority
    :allowed-values religion-question-values
    :text-fn (fn [value]
               (str "Is "
                    (str/capitalize (str/replace value "_" " "))
                    " the major religion in your country?"))}
   {:attribute :main_language_family
    :text-fn (fn [value]
               (str "Is "
                    (str/capitalize (str/replace value "_" " "))
                    " the main language family of your country?"))}])

(def is-your-country-attributes
  [:an_island_or_archipelago
   :landlocked
   :in_latin_america
   :on_equator
   :fully_in_the_southern_hemisphere
   :on_the_sahara_desert
   :crossed_by_danube
   :crossed_by_nile
   :a_monarchy
   :a_federation])

(def does-your-country-attributes
  [:have_coast_on_the_pacific_ocean
   :have_coast_on_the_indian_ocean
   :have_coast_on_the_atlantic_ocean
   :have_coast_on_the_arctic_ocean
   :have_coast_on_the_caspian_sea
   :have_coast_on_the_mediterranean_sea
   :have_nuclear_weapons
   :drive_on_the_left_side_of_the_road
   :claim_land_in_antarctica])

(def flag-shape-attributes
  [:horizontal_tricolor
   :vertical_tricolor])

(def flag-symbol-attributes
  [:cross_flag
   :crescent_flag
   :star_flag])

(defn format-value [value]
  (str/capitalize (str/replace value "_" " ")))

(defn format-attribute [attribute]
  (str/replace (name attribute) "_" " "))

(defn distinct-attribute-values [countries attribute]
  (sort (distinct (remove nil? (map attribute countries)))))

(defn build-enum-question [attribute value]
  {:kind :enum
   :attribute attribute
   :value value})

(defn build-boolean-question [attribute]
  {:kind :boolean
   :attribute attribute})

(defn build-numeric-question [attribute operator threshold]
  {:kind :numeric
   :attribute attribute
   :operator operator
   :threshold threshold})

(defn build-membership-question [attribute value]
  {:kind :membership
   :attribute attribute
   :value value})

(defn distinct-membership-values [countries attribute]
  (sort
    (distinct
      (mapcat attribute countries))))

(defn generate-membership-questions [countries attribute]
  (map
    (fn [value]
      (build-membership-question attribute value))
    (distinct-membership-values countries attribute)))

(defn generate-enum-questions [countries definition]
  (let [attribute (:attribute definition)
        allowed-values (:allowed-values definition)
        values (distinct-attribute-values countries attribute)
        values-to-use (if allowed-values
                        (filter allowed-values values)
                        values)]
    (map (fn [value]
           (build-enum-question attribute value))
         values-to-use)))

(defn generate-boolean-questions [attributes]
  (map (fn [attribute]
         (build-boolean-question attribute))
       attributes))

(defn generate-country-questions [countries]
  (concat
    (mapcat (fn [definition]
              (generate-enum-questions countries definition))
            enum-question-definitions)
    (generate-boolean-questions is-your-country-attributes)
    (generate-boolean-questions does-your-country-attributes)
    (generate-boolean-questions flag-shape-attributes)
    (generate-boolean-questions flag-symbol-attributes)
    (generate-membership-questions countries :regions)
    (generate-membership-questions countries :organizations)
    [(build-boolean-question :capital_is_largest)]))

(defn median [numbers]
  (let [sorted-numbers (sort numbers)
        count-numbers (count sorted-numbers)
        middle-index (quot count-numbers 2)]
    (if (odd? count-numbers)
      (nth sorted-numbers middle-index)
      (/ (+ (nth sorted-numbers (dec middle-index))
            (nth sorted-numbers middle-index))
         2.0))))

(defn median-for-attribute [countries attribute]
  (let [values (remove nil? (map attribute countries))]
    (when (seq values)
      (median values))))

(defn normalize-population-threshold [value]
  (if (>= value 1000000)
    (* 1000000 (quot (long value) 1000000))
    (* 100000 (quot (long value) 100000))))

(defn normalize-area-threshold [value]
  (if (>= value 1000)
    (* 1000 (quot (long value) 1000))
    (* 100 (quot (long value) 100))))

(defn whole-number? [value]
  (== value (Math/floor (double value))))

(defn format-population-threshold [value]
  (if (>= value 1000000)
    (str (quot (long value) 1000000) " million")
    (str (quot (long value) 1000) " thousand")))

(defn format-area-threshold [value]
  (str (long value) " km2"))

(defn format-border-threshold [value]
  (if (whole-number? value)
    (str (long value))
    (str value)))

(defn fallback-numeric-questions [countries]
  (let [population-median (median-for-attribute countries :population)
        area-median (median-for-attribute countries :area)
        borders-median (median-for-attribute countries :number_of_bordering_countries)
        normalized-population (when population-median
                                (normalize-population-threshold population-median))
        normalized-area (when area-median
                          (normalize-area-threshold area-median))]
    (remove nil?
            [(when normalized-population
               (build-numeric-question :population :greater-than normalized-population))
             (when normalized-population
               (build-numeric-question :population :less-than normalized-population))
             (when normalized-area
               (build-numeric-question :area :greater-than normalized-area))
             (when normalized-area
               (build-numeric-question :area :less-than normalized-area))
             (when borders-median
               (build-numeric-question :number_of_bordering_countries :greater-than borders-median))
             (when borders-median
               (build-numeric-question :number_of_bordering_countries :less-than borders-median))])))

(defn random-fallback-question [countries]
  (let [questions (vec (fallback-numeric-questions countries))]
    (when (seq questions)
      (rand-nth questions))))

(defn question-text [question]
  (let [kind (:kind question)
        attribute (:attribute question)
        value (:value question)]
    (cond
      (= kind :enum)
      (let [definition (first (filter (fn [definition]
                                        (= (:attribute definition) attribute))
                                      enum-question-definitions))
            text-fn (:text-fn definition)]
        (text-fn value))

      (and (= kind :membership)
           (= attribute :regions))
      (str "Is your country in the " value " region?")

      (and (= kind :membership)
           (= attribute :organizations))
      (str "Is your country a member of " value "?")

      (and (= kind :numeric)
           (= attribute :population)
           (= (:operator question) :greater-than))
      (str "Is the population of your country greater than "
           (format-population-threshold (:threshold question))
           "?")

      (and (= kind :numeric)
           (= attribute :population)
           (= (:operator question) :less-than))
      (str "Is the population of your country smaller than "
           (format-population-threshold (:threshold question))
           "?")

      (and (= kind :numeric)
           (= attribute :area)
           (= (:operator question) :greater-than))
      (str "Is the area of your country greater than "
           (format-area-threshold (:threshold question))
           "?")

      (and (= kind :numeric)
           (= attribute :area)
           (= (:operator question) :less-than))
      (str "Is the area of your country smaller than "
           (format-area-threshold (:threshold question))
           "?")

      (and (= kind :numeric)
           (= attribute :number_of_bordering_countries)
           (= (:operator question) :greater-than))
      (str "Does your country border more than "
           (format-border-threshold (:threshold question))
           " countries?")

      (and (= kind :numeric)
           (= attribute :number_of_bordering_countries)
           (= (:operator question) :less-than))
      (str "Does your country border fewer than "
           (format-border-threshold (:threshold question))
           " countries?")

      (contains? (set is-your-country-attributes) attribute)
      (str "Is your country " (format-attribute attribute) "?")

      (contains? (set does-your-country-attributes) attribute)
      (str "Does your country " (format-attribute attribute) "?")

      (contains? (set flag-shape-attributes) attribute)
      (str "Is your country's flag a " (format-attribute attribute) "?")

      (contains? (set flag-symbol-attributes) attribute)
      (let [attribute-name (name attribute)
            cleaned-name (str/replace attribute-name "_flag" "")]
        (str "Does your country's flag have a " cleaned-name "?"))

      (= attribute :capital_is_largest)
      "Is the capital of your country also its largest city?"

      :else
      "Unknown question")))