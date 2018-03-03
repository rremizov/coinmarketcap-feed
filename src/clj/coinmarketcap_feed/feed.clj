(ns coinmarketcap-feed.feed
  (:require [camel-snake-kebab.core :refer [->kebab-case]]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.tools.logging :as logging])
  (:require [clj-http.client :as http]
            [clj-time.coerce :as tc]
            [clj-time.core :as t]
            [clj-time.format :as tf]))

(defrecord RSSChannel [title link description])
(defrecord RSSEntry [title link description pubDate])

(def newline "<br/>")

(defn- query-api [ticker]
  (-> (str "https://api.coinmarketcap.com/v1/ticker/" ticker "/?convert=USD")
      http/get
      :body
      (json/read-str :key-fn (comp ->kebab-case keyword))))

(defn- parse-last-updated [x]
  (-> x :last-updated Integer/parseInt (* 1000) java.util.Date.))

(defn- render-title [x]
  (format "%s (%s). Price: %s USD (%s%%)."
          (:name x)
          (:symbol x)
          (:price-usd x)
          (:percent-change-24h x)))

(defn- render-url [x]
  (format "https://coinmarketcap.com/currencies/%s/" (:name x)))

(defn- render-description [x]
  (format (str "Price: %s USD%s"
               "24h volume: %s USD%s"
               "24h change: %s%%%s"
               "7d change: %s%%%s")
          (:price-usd x)
          newline
          (:24h-volume-usd x)
          newline
          (:percent-change-24h x)
          newline
          (:percent-change-7d x)
          newline))

(defn- feed [ticker render-pub-date]
  (->> (query-api ticker)
       (map (juxt render-title render-url render-description render-pub-date))))

(defn daily [ticker]
  (apply vector
         (->RSSChannel (str ticker " Daily")
                       (str "http://localhost/daily/" ticker)
                       (str ticker " Daily"))
         (map #(apply ->RSSEntry %) (feed ticker (fn [_] (tc/to-date (t/today)))))))

(defn latest [ticker]
  (apply vector
         (->RSSChannel (str ticker " Latest")
                       (str "http://localhost/latest/" ticker)
                       (str ticker " Latest"))
         (map #(apply ->RSSEntry %) (feed ticker parse-last-updated))))
