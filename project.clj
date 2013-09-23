(defproject cljs-pong "0.0.1-SNAPSHOT"
  :description "A variation on the Thoughtbot Pong example written in ClojureScript"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.0"]]
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-ring "0.8.3"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild {
    :builds {
      :main {
        :source-paths ["src/cljs"]
        :compiler {:output-to "resources/public/js/cljs_pong.js"
                   :optimizations :simple
                   :pretty-print true}
        :jar true}}}
  :main cljs-pong.server
  :ring {:handler cljs-pong.server/app})

