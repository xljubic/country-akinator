(ns country-akinator.questions-test
  (:require [midje.sweet :refer :all]
            [country-akinator.questions :as questions]
            [country-akinator.game :as game]))

(def serbia
  {:id 1
   :name "Serbia"
   :continent "europe"
   :religion_majority "christianity"
   :main_language_family "slavic"
   :landlocked true
   :population 6600000
   :area 88361
   :number_of_bordering_countries 8
   :regions #{"Southern Europe" "Eastern Europe"}
   :organizations #{"BRICS"}})

(def japan
  {:id 2
   :name "Japan"
   :continent "asia"
   :religion_majority "buddhism"
   :main_language_family "japonic"
   :an_island_or_archipelago true
   :population 123000000
   :area 377975
   :number_of_bordering_countries 0
   :regions #{"Eastern Asia"}
   :organizations #{"Group of Seven" "OECD"}})

(def brazil
  {:id 3
   :name "Brazil"
   :continent "south_america"
   :religion_majority "christianity"
   :main_language_family "romance"
   :have_coast_on_the_atlantic_ocean true
   :population 203000000
   :area 8515767
   :number_of_bordering_countries 10
   :regions #{"South America" "Latin America"}
   :organizations #{"BRICS"}})

(def test-countries [serbia japan brazil])

(fact "generate-country-questions includes europe continent question"
      (some (fn [question]
              (= question
                 {:kind :enum :attribute :continent :value "europe"}))
            (questions/generate-country-questions test-countries))
      => truthy)

(fact "generate-country-questions includes asia continent question"
      (some (fn [question]
              (= question
                 {:kind :enum :attribute :continent :value "asia"}))
            (questions/generate-country-questions test-countries))
      => truthy)

(fact "question-text formats continent question correctly"
      (questions/question-text
        {:kind :enum :attribute :continent :value "europe"})
      => "Is your country in Europe?")

(fact "question-text formats boolean question correctly"
      (questions/question-text
        {:kind :boolean :attribute :landlocked})
      => "Is your country landlocked?")

(fact "matches-question? returns true for matching enum question"
      (game/matches-question?
        serbia
        {:kind :enum :attribute :continent :value "europe"})
      => true)

(fact "matches-question? returns false for non-matching enum question"
      (game/matches-question?
        japan
        {:kind :enum :attribute :continent :value "europe"})
      => false)

(fact "matches-question? returns true for matching boolean question"
      (game/matches-question?
        serbia
        {:kind :boolean :attribute :landlocked})
      => true)

(fact "matches-question? returns false for non-matching boolean question"
      (game/matches-question?
        japan
        {:kind :boolean :attribute :landlocked})
      => false)

(fact "apply-answer keeps only matching countries for yes"
      (vec
        (game/apply-answer
          test-countries
          {:kind :enum :attribute :continent :value "europe"}
          :yes))
      => [serbia])

(fact "apply-answer keeps only non-matching countries for no"
      (vec
        (game/apply-answer
          test-countries
          {:kind :enum :attribute :continent :value "europe"}
          :no))
      => [japan brazil])

(fact "apply-answer keeps all countries for dont-know"
      (vec
        (game/apply-answer
          test-countries
          {:kind :enum :attribute :continent :value "europe"}
          :dont-know))
      => [serbia japan brazil])

(fact "fallback-numeric-questions generates six numeric fallback questions"
      (count (questions/fallback-numeric-questions test-countries))
      => 6)

(fact "fallback-numeric-questions creates numeric questions"
      (every? #(= :numeric (:kind %))
              (questions/fallback-numeric-questions test-countries))
      => true)

(fact "random-fallback-question returns one of generated fallback questions"
      (let [fallback-questions (questions/fallback-numeric-questions test-countries)
            result (questions/random-fallback-question test-countries)]
        (contains? (set fallback-questions) result))
      => true)

(fact "question-text formats population fallback question"
      (questions/question-text
        {:kind :numeric
         :attribute :population
         :operator :greater-than
         :threshold 23000000})
      => "Is the population of your country greater than 23 million?")

(fact "question-text formats area fallback question"
      (questions/question-text
        {:kind :numeric
         :attribute :area
         :operator :less-than
         :threshold 900})
      => "Is the area of your country smaller than 900 km²?")

(fact "question-text formats bordering countries fallback question"
      (questions/question-text
        {:kind :numeric
         :attribute :number_of_bordering_countries
         :operator :greater-than
         :threshold 3})
      => "Does your country border more than 3 countries?")

(fact "generate-country-questions includes region membership question"
      (some (fn [question]
              (= question
                 {:kind :membership
                  :attribute :regions
                  :value "South America"}))
            (questions/generate-country-questions test-countries))
      => truthy)

(fact "generate-country-questions includes organization membership question"
      (some (fn [question]
              (= question
                 {:kind :membership
                  :attribute :organizations
                  :value "BRICS"}))
            (questions/generate-country-questions test-countries))
      => truthy)

(fact "question-text formats region membership question correctly"
      (questions/question-text
        {:kind :membership
         :attribute :regions
         :value "South America"})
      => "Is your country in the South America region?")

(fact "question-text formats organization membership question correctly"
      (questions/question-text
        {:kind :membership
         :attribute :organizations
         :value "BRICS"})
      => "Is your country a member of BRICS?")

(fact "matches-question? returns true for matching region membership question"
      (game/matches-question?
        brazil
        {:kind :membership
         :attribute :regions
         :value "South America"})
      => true)

(fact "matches-question? returns false for non-matching region membership question"
      (game/matches-question?
        japan
        {:kind :membership
         :attribute :regions
         :value "South America"})
      => false)

(fact "matches-question? returns true for matching organization membership question"
      (game/matches-question?
        brazil
        {:kind :membership
         :attribute :organizations
         :value "BRICS"})
      => true)

(fact "apply-answer filters countries for organization membership question"
      (vec
        (game/apply-answer
          test-countries
          {:kind :membership
           :attribute :organizations
           :value "BRICS"}
          :yes))
      => [serbia brazil])