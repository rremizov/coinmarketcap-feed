(ns coinmarketcap-feed.routes.feeds
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.logging :as logging])
  (:require [compojure.api.sweet :refer :all]
            [muuntaja.core :as muuntaja]
            [ring.util.http-response :refer :all]
            [ring.util.request :as req]
            [schema.core :as s])
  (:require [coinmarketcap-feed.formats]
            [coinmarketcap-feed.feed :as feed]))

(defapi feeds-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}

   :formats coinmarketcap-feed.formats/default-formats}

  (GET "/daily/:ticker/" request
       :path-params [ticker :- s/Str]
       :return s/Any
       :summary "Cryptocurrency Market Capitalizations"
       (ok (feed/daily ticker))))
