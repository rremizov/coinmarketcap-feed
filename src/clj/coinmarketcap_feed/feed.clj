(ns coinmarketcap-feed.feed
  (:require [camel-snake-kebab.core :refer [->kebab-case]]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.tools.logging :as logging])
  (:require [clj-http.client :as http]
            [clj-time.coerce :as tc]
            [clj-time.core :as t]
            [clj-time.format :as tf])
  (:require [coinmarketcap-feed.config :refer [env]]))

(defrecord RSSChannel [title link description])
(defrecord RSSEntry [title link description pubDate guid])

(def newline-tag "<br/>")

(defn- query-api [ticker]
  (-> (str "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=" ticker)
      (http/get {:headers {"X-CMC_PRO_API_KEY" (env :coinmarketcap-api-key)}})
      :body
      (json/read-str :key-fn (comp ->kebab-case keyword))
      :data
      (get (keyword (.toLowerCase (str ticker))))))

(defn- parse-last-updated [x]
  (tc/to-date (tf/parse (tf/formatters :date-time) (:last-updated x))))

(defn- render-title [x]
  (format "%s (%s). Price: %s USD (%s%%)."
          (:name x)
          (:symbol x)
          (-> x :quote :usd :price)
          (-> x :quote :usd :percent-change-24h)))

(defn- render-url [x]
  (format "https://coinmarketcap.com/currencies/%s/" (:name x)))

(defn- render-description [x]
  (format (str "Price: %.2f USD%s"
               "24h volume: %.2f USD%s"
               "24h change: %.2f%%%s"
               "7d change: %.2f%%%s")
          (-> x :quote :usd :price)
          newline-tag
          (-> x :quote :usd :volume-24h)
          newline-tag
          (-> x :quote :usd :percent-change-24h)
          newline-tag
          (-> x :quote :usd :percent-change-7d)
          newline-tag))

(defn- render-guid [render-pub-date x]
  [{:isPermaLink false}
   (str (render-url x) " at " (render-pub-date x))])

(defn- feed [ticker render-pub-date]
  [((juxt render-title
          render-url
          render-description
          render-pub-date
          (partial render-guid render-pub-date))
    (query-api ticker))])

(defn daily [ticker]
  (apply vector
         (->RSSChannel (str ticker " Daily")
                       (str "https://coinmarketcap.com/" ticker)
                       (str ticker " Daily"))
         (map #(apply ->RSSEntry %) (feed ticker (fn [_] (tc/to-date (t/today)))))))

(defn latest [ticker]
  (apply vector
         (->RSSChannel (str ticker " Latest")
                       (str "https://coinmarketcap.com/" ticker)
                       (str ticker " Latest"))
         (map #(apply ->RSSEntry %) (feed ticker parse-last-updated))))