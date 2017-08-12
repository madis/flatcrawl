(ns flatcrawl.import-test
  (:require [clojure.test :refer :all]
            [flatcrawl.import :refer :all]
            [net.cgrand.enlive-html :as html]))

(def page-fixture "fixtures/search_results_page-1.html")
(def page-html (html/html-resource page-fixture))
(def simple-template (html/html-resource "fixtures/simple.html"))

(deftest page->records-testing
  (testing "Extract elements from HTML to mappings of rows"
  (let [expected-records [{:title "Heading1" :desc "Desc1"} {:title "Heading2" :desc "Desc2"}]
        page simple-template
        selectors {:title [:h1] :desc [:p]}]
  (is (= expected-records (page->records page selectors))))))
