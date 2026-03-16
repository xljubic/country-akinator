(ns country-akinator.questions-test
  (:require [midje.sweet :refer :all]
            [country-akinator.questions :as questions]
            [country-akinator.game :as game]))

(def serbia
  {:id 151
   :name "Serbia"
   :continent "europe"
   :religion_majority "christianity"
   :main_language_family "indo_european"
   :landlocked true
   :a_monarchy false
   :horizontal_tricolor true})

(def japan
  {:id 81
   :name "Japan"
   :continent "asia"
   :religion_majority "other"
   :main_language_family "japonic"
   :landlocked false
   :a_monarchy true
   :horizontal_tricolor false})

(def test-countries [serbia japan])

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
      => [japan])

(fact "apply-answer keeps all countries for dont-know"
      (vec
        (game/apply-answer
          test-countries
          {:kind :enum :attribute :continent :value "europe"}
          :dont-know))
      => [serbia japan])
