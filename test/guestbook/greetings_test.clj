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
