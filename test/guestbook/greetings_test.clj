(ns guestbook.greetings-test
  (:use
    clojure.contrib.test-is
    appengine-clj.test-utils)
  (:require
    [guestbook.greetings      :as greetings]
    [appengine-clj.datastore  :as ds])
  (:import
    (com.google.appengine.api.datastore Query)))


; Note that each dstest runs in a fresh in-memory Datastore.
(dstest can-create-a-greeting
  (greetings/create "Hello from this test." "johnhume")
  (let [all-greetings (ds/find-all (Query. "Greeting"))
        my-greeting (first all-greetings)]
    (is (= 1 (count all-greetings)))
    (is (= "johnhume" (:author my-greeting)))
    (is (= "Hello from this test." (:content my-greeting)))))

(dstest create-adds-date
  (let [created-item (greetings/create "Yup." "johnhume")]
    (is (not (nil? (created-item :date))))))

(dstest find-all-sorts-by-date-from-oldest-to-newest
  (let [recent-item (greetings/create "Hiyo." "hume")
        more-recent-item (greetings/create "Yup." "hume")
        old-item (ds/create {:kind "Greeting" :content "It's 1970!" :author "hume" :date (java.util.Date. (long 0))})]
    (is (= ["It's 1970!" "Hiyo." "Yup."]
           (map :content (greetings/find-all))))))
