(ns guestbook.servlet.GuestbookServlet
  (:require
    [compojure.http :as http]
    [compojure.html :as html])
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory))
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(defn show-guestbook [user-service user]
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
            " to include your name with your greeting when you post.)"])]]))

(http/defservice "-"
  (let [user-service (UserServiceFactory/getUserService)
        user (.getCurrentUser user-service)]
    (http/GET "/"
      (show-guestbook user-service user))))
