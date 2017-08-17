(ns flatcrawl.db
  (:require [clojure.java.jdbc :as sql]
            [environ.core :refer [env]]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]
            [clojure.string :as str]))

(def db {:dbtype "postgresql"
         :dbname (env :db-name)
         :host "localhost"
         :user (env :db-user)
         :password (env :db-pass)})

(defn snakeify-map-keys [map]
  (reduce (fn [r, [k, v]] (assoc r (str/replace (name k) "-" "_") v)) {} map))

(defn add-property [property]
  (let [now (tc/to-sql-date (time/now))
        timestamps {:created_at now :updated_at now}
        db-property (snakeify-map-keys property)]
    (sql/insert! db "properties" (merge db-property timestamps))))

(defn get-properties []
  (sql/query db ["SELECT * FROM properties"]))
