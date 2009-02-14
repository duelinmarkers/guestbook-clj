(ns guestbook.servlet.GuestbookServlet
  (:require
    [compojure.http :as http]
    [compojure.html :as html]
    [guestbook.greeting :as greeting])
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
  (let [[user-service user] (user-info)
        greetings (greeting/find-all)]
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
          (if (empty? greetings)
            [:p "The guestbook has no messages."]
            (map
              (fn [g] [:div
                        [:p
                          (if (g :author)
                            [:strong (g :author)]
                            "An anonymous guest")
                          " wrote:"]
                        [:blockquote (g :content)]])
              greetings))
          [:form {:action "/sign" :method "POST"}
            [:div [:textarea {:name "content" :rows "3" :cols "60"}]]
            [:div [:input {:type "submit" :value "Post Greeting"}]]]]])))

(defn sign-guestbook [params]
  (let [[_ user] (user-info)]
    (greeting/create (params :content) (if user (.getNickname user)))
    (http/redirect-to "/")))

(http/defservice "-"
  (http/POST "/sign"
    (sign-guestbook params))
  (http/GET "/"
    (show-guestbook))
  (http/ANY "*"
    [404 "Not found!"]))
