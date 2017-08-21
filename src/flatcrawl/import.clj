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

(defn sanitize [string]
  (string/trim string))

(defn extract-from-snippet
  "Returns matches for selectors on a page running them through processor function"
  [snippet selector processor]
  (processor (html/select snippet selector)))

(defn columns-to-record-rows [selector-rows-map]
  (->> selector-rows-map
       vals
       (apply map vector)
       (map #(zipmap (keys selector-rows-map) %))))

(defn named-columns-from-snippet [selectors snippet]
  "Takes a map with selectors & processor as values and a snippet.
  Returns matches as values. E.g.
    {:title [[:td.title], get-text]} :body [[:td.body], get-text]} ->
      {:title \"I am title\" :body \"I am body\""
  (map (fn [[key [selector processor]]]
         [key (extract-from-snippet snippet selector processor)]) selectors))

(defn to-mapza [result [key value]] (assoc result key value))

(defn extract-record [selectors snippet]
  "Extracts data into a map from a snippet according to selectors"
  (let [snippet-extractor (partial named-columns-from-snippet selectors)]
    (reduce to-mapza {} (snippet-extractor snippet))))

(defn attr-getter [attr-name node & others]
  (first (html/attr-values node attr-name)))

(defn get-text [nodes] (apply str (map sanitize (map html/text nodes))))
(defn get-int [[node & others]] ((comp #(Integer. %) #(re-find #"\d+" %) get-text) [node]))
(defn get-link [[node & others]] (first (html/attr-values node :href)))
(defn get-id [node & others] (Integer. (attr-getter :id (first node))))

(def listing-selectors
  {:external-id [[:tr] get-id]
   :title [[:td.object-name :h2.object-title] get-text]
   :short-desc [[:td.object-name :p.object-excerpt] get-text]
   :link [[:td.object-name :h2.object-title :a] get-link]
   :rooms [[:td.object-rooms] get-int]
   :area [[:td.object-m2] get-int]
   :price [[:td :p.object-price-value] get-int]})

(defn kv-search->records [page]
  "Maps kv.ee search page to list of records based on selectors"
  (let [ad-row-selector [:tr.object-type-apartment]
        rows (html/select page ad-row-selector)
        kv-ad-extractor (partial extract-record listing-selectors)
        not-nil-rows (filter #((complement nil?) (html/attr-values % :id)) rows)]
    (map kv-ad-extractor not-nil-rows)))
