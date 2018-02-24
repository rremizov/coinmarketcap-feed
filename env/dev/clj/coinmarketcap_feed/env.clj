(ns coinmarketcap-feed.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [coinmarketcap-feed.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[coinmarketcap-feed started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[coinmarketcap-feed has shut down successfully]=-"))
   :middleware wrap-dev})
