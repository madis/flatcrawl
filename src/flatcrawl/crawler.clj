(ns flatcrawl.crawler
  (:gen-class)
  (:require [flatcrawl.import :as import]
            [flatcrawl.db :as db]
            [flatcrawl.services :refer (->Service)]
            [clojure.core.async :as async :refer [thread]]))

(defn exist-in-db? [property]
  (not-empty (db/find-by-external-id (:external-id property))))

(defn crawl-and-update []
  (let [results (import/kv-search->records (import/search-kv-ee))]
    (doseq [property results]
      (if (exist-in-db? property)
        (db/update-property property)
        (db/add-property property)))))

(def ^:dynamic *crawler-channel* nil)
(def ^:dynamic *crawler-stop* false)

(declare crawl-kv-ee-indefinitely)
(defn crawl-in-background [] (Thread. crawl-kv-ee-indefinitely))

(defn swap-crawler [new-crawler]
  (def ^:dynamic *crawler-channel* (crawl-in-background)))

(defn crawl-kv-ee-indefinitely []
  (loop []
    (if *crawler-stop*
      (do (println "Stopping crawling")
          (swap-crawler nil))
      (do (println "Crawling...")
          (crawl-and-update)
          (println "Finished crawling. Will sleep")
          (Thread/sleep 10000)
          (recur)))))

(defn start []
  "Starts crawler if it's not running. Returns channel"
  (if (nil? *crawler-channel*)
    (do (println "Starting crawler")
        (swap-crawler (crawl-in-background))
        (.start *crawler-channel*)
        (println "Crawler started"))
    *crawler-channel*))

(defn stop [] (if *crawler-channel*
                (do (println "Stopping crawler")
                    (def ^:dynamic *crawler-stop* true)
                    (println "Crawler stopped"))))

(defn status [] (if *crawler-channel* :running :stopped))

(defn get-service [] (->Service "crawler" start stop status))

(defn -main [] "Starts crawler and waits" (crawl-kv-ee-indefinitely))
