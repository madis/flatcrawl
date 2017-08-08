(ns flatcrawl.import-test
  (:require [clojure.test :refer :all]
            [flatcrawl.import :refer :all]
            [net.cgrand.enlive-html :as html]))

(def page-fixture "fixtures/search_results_page-1.html")
(def page-html (html/html-resource page-fixture))

(deftest get-titles-test
  (is (= "1. Harjumaa, Tallinn, Kristiine, Sõpruse pst 29" (first (get-titles page-html)))))
