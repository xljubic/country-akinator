(ns country-akinator.scoring-test
  (:require [midje.sweet :refer :all]
            [country-akinator.scoring :as scoring]))

(def serbia
  {:id 151
   :name "Serbia"
   :continent "europe"
   :landlocked true})

(def japan
  {:id 81
   :name "Japan"
   :continent "asia"
   :landlocked false})

(def hungary
  {:id 73
   :name "Hungary"
   :continent "europe"
   :landlocked true})

(def brazil
  {:id 24
   :name "Brazil"
   :continent "south_america"
   :landlocked false})

(def test-countries [serbia japan hungary brazil])

(fact "count-yes-for-question counts matching countries"
      (scoring/count-yes-for-question
        test-countries
        {:kind :enum :attribute :continent :value "europe"})
      => 2)

(fact "count-no-for-question counts non-matching countries"
      (scoring/count-no-for-question
        test-countries
        {:kind :enum :attribute :continent :value "europe"})
      => 2)

(fact "question-score is higher for a balanced split"
      (scoring/question-score
        test-countries
        {:kind :enum :attribute :continent :value "europe"})
      => 2)

(fact "question-score is lower for an unbalanced split"
      (scoring/question-score
        test-countries
        {:kind :enum :attribute :continent :value "asia"})
      => 1)

(fact "question-score is zero when all countries give the same answer"
      (scoring/question-score
        test-countries
        {:kind :boolean :attribute :capital_is_largest})
      => 0)
