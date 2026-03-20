(ns country-akinator.repository
  (:require [clojure.java.jdbc :as jdbc]
            [country-akinator.db :as db]))

(defn load-country-regions []
  (jdbc/query
    (db/db-spec)
    ["SELECT cr.country_id, r.name AS region_name
      FROM country_regions cr
      JOIN regions r ON cr.region_id = r.id"]))

(defn load-country-organizations []
  (jdbc/query
    (db/db-spec)
    ["SELECT co.country_id, o.name AS organization_name
      FROM country_organizations co
      JOIN organizations o ON co.organization_id = o.id"]))

(defn group-regions-by-country [rows]
  (reduce
    (fn [result row]
      (update result
              (:country_id row)
              (fnil conj #{})
              (:region_name row)))
    {}
    rows))

(defn group-organizations-by-country [rows]
  (reduce
    (fn [result row]
      (update result
              (:country_id row)
              (fnil conj #{})
              (:organization_name row)))
    {}
    rows))

(defn enrich-countries-with-memberships [countries]
  (let [regions-by-country (group-regions-by-country (load-country-regions))
        organizations-by-country (group-organizations-by-country (load-country-organizations))]
    (map
      (fn [country]
        (assoc
          (assoc country
            :regions
            (get regions-by-country (:id country) #{}))
          :organizations
          (get organizations-by-country (:id country) #{})))
      countries)))


(defn load-all-countries []
  (let [countries (jdbc/query (db/db-spec) ["SELECT * FROM countries ORDER BY name"])]
    (enrich-countries-with-memberships countries)))

(defn load-country-by-id [id]
  (first
    (filter #(= (:id %) id)
            (load-all-countries))))

(defn load-countries-by-id []
  (let [countries (load-all-countries)]
    (into {} (map (fn [country] [(:id country) country]) countries))))
