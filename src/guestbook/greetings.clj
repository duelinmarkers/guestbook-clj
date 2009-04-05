(ns guestbook.greetings
  (:require [appengine-clj.datastore :as ds])
  (:import (com.google.appengine.api.datastore
              DatastoreServiceFactory
              Entity
              Query)))


(defn create [content author]
  (ds/create {:kind "Greeting" :author author :content content}))

(defn find-all []
  (ds/find-all (doto (Query. "Greeting") (.addSort "date"))))
