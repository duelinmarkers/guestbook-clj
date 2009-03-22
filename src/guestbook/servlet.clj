(ns guestbook.servlet
  (:require
    [compojure.http         :as http]
    [compojure.http.routes  :as routes]
    [compojure.html         :as html]
    [compojure.html.form-helpers :as form]
    [compojure.html.page-helpers :as page]
    [guestbook.greeting     :as greeting])
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory)
    (java.util.logging Logger))
  (:gen-class
    :extends javax.servlet.http.HttpServlet))


(defn user-info []
  (let [user-service (UserServiceFactory/getUserService)]
    [user-service (.getCurrentUser user-service)]))

(defn show-guestbook []
  (let [[user-service user] (user-info)
        greetings (greeting/find-all)]
    (html/html
      [:html
        [:head
          [:title "Guestbook"]
          (page/include-css "/stylesheets/main.css")]
        [:body
          (if user
            [:p (str "Hello, " (.getNickname user) "! (You can ")
              (page/link-to (.createLogoutURL user-service "/") "sign out")
              ".)"]
            [:p "Hello! (You can "
              (page/link-to (.createLoginURL user-service "/") "sign in")
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
          (form/form-to [POST "/sign"]
            [:div
              (form/text-area {:rows "3" :cols "60"}
                "content" "")]
            [:div
              (form/submit-button "Post Greeting")])]])))

(defn sign-guestbook [params]
  (let [[_ user] (user-info)]
    (greeting/create (params :content) (if user (.getNickname user)))
    (http/redirect-to "/")))

(routes/defroutes guestbook-app
  (http/POST "/sign"
    (sign-guestbook params))
  (http/GET "/"
    (show-guestbook))
  (http/ANY "*"
    [404 "Not found!"]))

(http/defservice guestbook-app)
