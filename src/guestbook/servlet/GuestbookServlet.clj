(ns guestbook.servlet.GuestbookServlet
  (:require [compojure.http :as http])
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory))
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(http/defservice "-"
  (http/GET "/*"
    (let [user-service (UserServiceFactory/getUserService)
          user (.getCurrentUser user-service)]
      (if user
        [(str "Hello, " (.getNickname user))]
        (http/redirect-to (.createLoginURL user-service (.getRequestURI request)))))))
