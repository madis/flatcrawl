(ns flatcrawl.web
  (:require [compojure.core :refer [defroutes GET]]
            [ring.adapter.jetty :as ring]
            [ring.util.response :as rr]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup.page :as page]
            [flatcrawl.db :as db]
            [ring.middleware.defaults :refer :all]))

(defn summary-presenter []
  (let [properties (db/get-properties)
        numeric-properties (map #(select-keys % [:area :price]) properties)
        sums (apply merge-with + numeric-properties)
        total (count properties)]
    {:total-crawls 1
     :sums sums
     :total total
     :average-price (+ (:price sums) total)
     :average-area (+ (:area sums) total)
     }))

(defn status-summary []
  (let [s (summary-presenter)]
    [:div
     [:h1 "Crawl results"]
     [:ul
      [:li (str "Total crawls made: " (s :total-crawls))]
      [:li (str "Properties in the database: " (s :properties-count))]
      [:li (str "Average price: " (s :average-price) "€")]
      [:li (str "Average size: " (s :average-area) "m²")]]]))

(defn index []
  (rr/content-type
    (rr/response (page/html5
                   [:head [:title "Flatcrawl status"]]
                   [:body [:div {:id "content"} (status-summary)]])) "text/html; charset=utf-8"))

(defroutes routes
  (GET "/" [] (index)))

(def app (wrap-defaults (wrap-reload #'routes) site-defaults))

(defn -main []
  (ring/run-jetty app {:port 8080 :join? false}))
