(ns flatcrawl.import
  (:require [clj-http.client :as client]
            [clojure.xml :as xml]
            [clojure.string :as string]
            [net.cgrand.enlive-html :as html]))

(def tallinn-districts
  (let [districts-with-codes
        [["Haabersti",13237]
         ["Kadriorg",66157]
         ["Kesklinn",13238]
         ["Kristiine",13239]
         ["Lasnamäe",13240]
         ["Mustamäe",13241]
         ["Nõmme",13242]
         ["Pirita",13243]
         ["Põhja-Tallinn",13244]
         ["Vanalinn",400]]]
    (into {} districts-with-codes)))

(def search-url "http://kinnisvaraportaal-kv-ee.postimees.ee/")

(defn map->query [map]
  (clojure.string/join "&" (for [[k v] map] (str (name k) "=" (java.net.URLEncoder/encode (str v))))))

(defn request-url [url]
  "Makes GET request for a url (passed as string) and returns parsed hash map of dom"
  (html/html-resource (java.net.URL. url)))

(defn query-params []
  {"act" "search.simple"
   "last_deal_type" 2
   "page" 1
   "orderby" "ob"
   "page_size" 50
   "deal_type" 2
   "dt_select" 2
   "country" 1
   "parish" 421
   "city[0]" 66157
   "city[1]" 13238
   "city[2]" 13239
   "city[3]" 13244
   "city[4]" 400
   "rooms_max" 3
   "price_min" 300
   "price_max" 750})

(defn make-query-url [params]
  (str search-url "?" (map->query (query-params))))

(defn search-kv-ee
  "Searches with parameters"
  []
  (let [query-params (query-params)]
    (request-url (make-query-url {}))))

(defn sanitize-titles [titles]
  (map string/trim (map html/text titles)))

(def title-selectors [:tr.object-type-apartment :td.object-name :h2.object-title])

(defn get-titles [page]
 (sanitize-titles (html/select page title-selectors)))

