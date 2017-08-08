(defproject flatcrawl "0.1.0-SNAPSHOT"
  :description "Crawler for flat price info"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [clj-http "3.6.1"]
                 [enlive "1.1.6"]
                 [http-kit "2.3.0-alpha2"]]

  :main ^:skip-aot flatcrawl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
