(ns guestbook.greeting
  (:import (com.google.appengine.api.datastore
              DatastoreServiceFactory
              Entity
              Query)))


(defn create [content author]
  (let [data-service (DatastoreServiceFactory/getDatastoreService)]
    (.put data-service
      (doto (Entity. "Greeting")
        (.setProperty "author" author)
        (.setProperty "content" content)
        (.setProperty "date" (java.util.Date.))))))

(defn find-all []
  (let [data-service (DatastoreServiceFactory/getDatastoreService)
        results (.asIterable (.prepare data-service (doto (Query. "Greeting")
                                                      (.addSort "date"))))]
    (map (fn [entity]
      {:author (.getProperty entity "author") :content (.getProperty entity "content") :date (.getProperty entity "date")})
      results)))
