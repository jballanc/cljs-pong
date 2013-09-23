(ns cljs-pong.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :as resources]
            [ring.util.response :as response])
  (:gen-class))

(defn render-app []
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "<!DOCTYPE html>"
              "<html>"
              "<head>"
              "  <script src=\"js/cljs_pong.js\"></script>"
              "</head>"
              "<body></body>"
              "</html>")})

(defn handler [request]
  (render-app))

(def app
  (-> handler
      (resources/wrap-resource "public")))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))

