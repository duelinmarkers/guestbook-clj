(ns guestbook.servlet.GuestbookServlet
  (:use (compojure http html))
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(defservice "-"
  (ANY "/*"
    "Hello, world"))
