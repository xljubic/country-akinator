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
  (str/capitalize
    (str/replace value "_" " ")))

(defn format-attribute [attribute]
  (str/replace (name attribute) "_" " "))

(defn distinct-attribute-values [countries attribute]
  (sort
    (distinct
      (remove nil?
              (map attribute countries)))))

(defn build-enum-question [attribute value]
  {:kind :enum
   :attribute attribute
   :value value})

(defn build-boolean-question [attribute]
  {:kind :boolean
   :attribute attribute})

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
    [(build-boolean-question :capital_is_largest)]))

(defn question-text [question]
  (let [kind (:kind question)
        attribute (:attribute question)
        value (:value question)]
    (cond
      (= kind :enum)
      (let [definition (first
                         (filter (fn [definition]
                                   (= (:attribute definition) attribute))
                                 enum-question-definitions))
            text-fn (:text-fn definition)]
        (text-fn value))

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
