(defproject calbot "0.1.0-SNAPSHOT"
  :description "initially a commandline tool to talk to google calendar"
  :url "http://github.com/huiruru/calbot"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [google-apps-clj "0.6.1"]
                 [clj-time "0.13.0"]]
  :main ^:skip-aot calbot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
