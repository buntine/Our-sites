(ns sites-www.core
  (:import [java.io File])
  (:use compojure.core
        clojure.contrib.str-utils
        hiccup.core
        ring.adapter.jetty)
  (:require [compojure.route :as route]))

(def conf-path "/home/andrew/dev/clojure/balls")

(defn sites-from-conf-list []
  "Returns a sequence of strings, one for each file in 'conf-path'"
  (let [dir (File. conf-path)]
    (seq (filter #(re-matches #".*\.conf" %)
                 (.list dir)))))

(defn format-name [nam]
  "Formats the given name as a domain name"
  (re-sub #"\.conf$" "" nam))

(defn site-link [name]
  "Generates an HTML anchor for the given name"
  (html [:a {:href (str "http://" name)} name]))

(defn links [lst]
  "Returns an array of HTML anchors for the given sites list"
  (map #(html [:li (site-link (format-name %))])
       lst))

(defn listify [lst]
  "Returns an array of strings in the form of an HTML list"
  (html [:ul (str-join "" (links lst))]))

(defn sites-list []
  "Returns an array of sites from the contents of our Nginx
   configuration directory"
  (html [:h1 "Our sites"]
        (listify (sites-from-conf-list))))

(defroutes site
  (GET "/" [] (sites-list))
  (route/not-found "Page not found"))

(run-jetty site {:port 8080})
