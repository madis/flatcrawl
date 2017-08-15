(ns flatcrawl.core
  (:gen-class)
  (:require [flatcrawl.import :as import]
            [flatcrawl.db :as db]))

(defn crawl-and-update []
  (let [results (import/kv-search->records (import/search-kv-ee))]
    (doseq [property results] (db/add-property property))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Crawling kv.ee")
  (crawl-and-update))

