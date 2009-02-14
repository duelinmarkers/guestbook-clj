(ns guestbook.servlet.GuestbookServlet
  (:require [compojure.http :as http])
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(http/defservice "-"
  (http/GET "/*"
    "Hello, world"))
