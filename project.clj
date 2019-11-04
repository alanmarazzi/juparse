(defproject juparse "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.9.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [clj-commons/fs "1.5.1"]]
  :repl-options {:init-ns juparse.core}
  :main juparse.core
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-binplus "0.6.5"]]}}
  :bin {:name "juparse"})
