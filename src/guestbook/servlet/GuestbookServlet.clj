(ns guestbook.servlet.GuestbookServlet
  (:require
    [compojure.http :as http]
    [compojure.html :as html])
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory)
    (java.util.logging Logger))
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(def logger (Logger/getLogger "guestbook.servlet.GuestbookServlet"))

(defn user-info []
  (let [user-service (UserServiceFactory/getUserService)]
    [user-service (.getCurrentUser user-service)]))

(defn show-guestbook []
  (let [[user-service user] (user-info)]
    (html/html
      [:html
        [:head [:title "Guestbook"]]
        [:body
          (if user
            [:p (str "Hello, " (.getNickname user) "! (You can ")
              [:a {:href (.createLogoutURL user-service "/")} "sign out"]
              ".)"]
            [:p "Hello! (You can "
              [:a {:href (.createLoginURL user-service "/")} "sign in"]
              " to include your name with your greeting when you post.)"])
          [:form {:action "/sign" :method "POST"}
            [:div [:textarea {:name "content" :rows "3" :cols "60"}]]
            [:div [:input {:type "submit" :value "Post Greeting"}]]]]])))

(defn sign-guestbook [params]
  (let [[user-service user] (user-info)]
    (if user
      (.info logger (str "Greeting posted by " (.getNickname user) ": " (params :content)))
      (.info logger (str "Greeting posted anonymously: " (params :content))))
    (http/redirect-to "/")))

(http/defservice "-"
  (http/POST "/sign"
    (sign-guestbook params))
  (http/GET "/"
    (show-guestbook))
  (http/ANY "*"
    [404 "Not found!"]))
