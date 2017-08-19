(ns flatcrawl.core
  (:gen-class)
  (:require [flatcrawl.crawler :as crawler]
            [flatcrawl.web :as web]))

(def ^:dynamic *services* [(web/get-service) (crawler/get-service)])

(defn -main
  "Starts services in the background and then waits forever"
  [& args]
  (println "Starting flatcrawl")
  (doseq [service *services*] (.start service))
  (println "Services started. Will wait forever")
  (repeatedly #(Thread/sleep 1000)))

