(ns country-akinator.play-test
  (:require [midje.sweet :refer :all]
            [country-akinator.play :as play]))

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

(def test-countries [serbia japan hungary])

(fact "game-finished? returns false when more than one country remains"
      (play/game-finished? [serbia japan hungary brazil])
      => false)

(fact "game-finished? returns false when three countries remain"
      (play/game-finished? test-countries)
      => false)

(fact "game-finished? returns false when two countries remain"
      (play/game-finished? [serbia japan])
      => false)

(fact "game-finished? returns true when one country remains"
      (play/game-finished? [serbia])
      => true)

(fact "game-finished? returns true when no countries remain"
      (play/game-finished? [])
      => true)

(fact "remaining-questions excludes already asked questions"
      (let [asked [{:kind :enum :attribute :continent :value "europe"}]
            remaining (play/remaining-questions test-countries asked)]
        (contains? (set remaining)
                   {:kind :enum :attribute :continent :value "europe"})
        => false))

(fact "select-next-question returns a question that was not already asked"
      (let [asked [{:kind :enum :attribute :continent :value "europe"}]
            result (play/select-next-question test-countries asked)]
        (contains? (set asked) result)
        => false))