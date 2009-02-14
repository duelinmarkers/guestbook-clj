(ns guestbook.servlet.GuestbookServlet
  (:use (compojure http html))
  (:import javax.servlet.http.HttpServlet)
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(defservice "-"
  (ANY "/*"
    "Hello, world"))
