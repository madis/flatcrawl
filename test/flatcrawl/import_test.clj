(ns flatcrawl.import-test
  (:require [clojure.test :refer :all]
            [flatcrawl.import :refer :all :as i]
            [net.cgrand.enlive-html :as html]))

(def page-fixture "fixtures/search_results_page-1.html")
(def page-html (html/html-resource page-fixture))
(def simple-html (html/html-resource "fixtures/simple.html"))

(deftest extract-record-test
  (testing "Extract elements from HTML to mappings of rows"
    (let [expected-record {:title "Heading1" :desc "Desc1"}
          snippet (first (html/select simple-html [:li]))
          selectors {:title [[:h1] i/get-text] :desc [[:p] i/get-text]}]
      (is (= expected-record (extract-record selectors snippet))))))

(deftest get-number-test
  (let [number-node (take 1 (html/select simple-html [:span.number]))]
    (is (= 11 (get-int number-node)))))

(deftest kv-ee-ad-parsing-test
  (testing "Extracing correct data from real page"
    (let [flat-ad {:title "1. Harjumaa, Tallinn, Kristiine, Sõpruse pst 29"
                   :short-desc "Korrus 2/5, korteriomand, kivimaja, ehitatud 2008, valmis, elektripliit, dušš, rõdu , ...Kesklinna vahetus läheduses, Kristiine linnaosas üürile anda möbleeritud 2-toaline ..."
                   :link "http://kinnisvaraportaal-kv-ee.postimees.ee/kesklinna-vahetus-laheduses-kristiine-linnaosas-uu-2949473.html?nr=1&search_key=aff35c03ce26f180ec6cea6413be800f"
                   :rooms 2
                   :area 52
                   :price 550}
          ]
      (is (= flat-ad (first (kv-search->records page-html)))))))
