(ns country-akinator.answers)

(def yes :yes)
(def no :no)
(def dont-know :dont-know)

(def valid-answers #{yes no dont-know})

(defn valid-answer? [answer]
  (contains? valid-answers answer))
