(ns flatcrawl.noob
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clj-http.client :as client]))

(defn get-dom
  []
  (html/html-snippet (:body (client/get "https://dustorrent.com/tv/"))))

(defn extract-titles [dom]
  (map (comp first :content) (html/select dom [:a.cellMainLink])))

(defn -main
  []
  (let [titles (extract-titles (get-dom))]
    (println titles)))
