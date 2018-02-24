(ns user
  (:require [mount.core :as mount]
            coinmarketcap-feed.core))

(defn start []
  (mount/start-without #'coinmarketcap-feed.core/repl-server))

(defn stop []
  (mount/stop-except #'coinmarketcap-feed.core/repl-server))

(defn restart []
  (stop)
  (start))
