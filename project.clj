(defproject flatcrawl "0.1.0-SNAPSHOT"
  :description "Crawler for flat price info"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [clj-http "3.7.0"]
                 [enlive "1.1.6"]
                 [http-kit "2.3.0-alpha2"]
                 [org.postgresql/postgresql "42.1.4"]
                 [org.clojure/java.jdbc "0.7.0"]
                 [migratus "0.9.9"]
                 [environ "1.1.0"]
                 [clj-time "0.14.0"]
                 [ring/ring-jetty-adapter "1.6.2"]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.3.1"]
                 [org.clojure/core.async "0.3.443"]
                 [ring "1.6.2"]]

  :main ^:skip-aot flatcrawl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[migratus-lein "0.5.0"]
            [lein-environ "1.1.0"]
            [lein-ring "0.9.7"]]
  :ring {:handler flatcrawl.web/app
         :auto-reload? true
         :auto-refresh? true}
  :migratus {:store :database
             :migration-dir "migrations"
             :db {:classname "org.postgresql.Driver"
                  :subprotocol "postgres"
                  :subname (System/getenv "DB_NAME")
                  :user (System/getenv "DB_USER")
                  :password (System/getenv "DB_PASS")}})
