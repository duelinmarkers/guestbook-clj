(ns guestbook.servlet
  (:require
    [compojure.http         :as http]
    [compojure.http.routes  :as routes]
    [compojure.html.gen     :as html]
    [compojure.html.form-helpers :as form]
    [compojure.html.page-helpers :as page]
    [guestbook.greeting     :as greeting]
    [guestbook.clj-exercise :as clj-exercise])
  (:import
    (com.google.appengine.api.users User UserService UserServiceFactory))
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
            [:p "Hello, " (.getNickname user) "! (You can "
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
            [:div (form/text-area "content" "")]
            [:div (form/submit-button "Post Greeting")])
          (page/link-to "/exercise" "exercise clojure a bit")]])))

(defn sign-guestbook [params]
  (let [[_ user] (user-info)]
    (greeting/create (params :content) (if user (.getNickname user)))
    (http/redirect-to "/")))

(defn exercise []
  (let [[_ user] (user-info)
        [atom-value ref-value] (clj-exercise/show-off (if user (.getNickname user) "anon"))]
    (html/html [:html
      [:head
        [:title "Clojure on AppEngine: Atoms and Refs"]
        (page/include-css "/stylesheets/main.css")]
      [:body
        [:h1 "Atoms and Refs"]
        [:p "Each request to this page increments an atom, which starts at zero, 
            and updates a ref by adding to a vector of visitors and a leaving a timestamp.
            This is just here to illustrate that atoms and refs work.
            But you may see with repeated requests that they work a little strangely
            due to the distributed nature of AppEngine."]
        [:p "The current atom value is " (html/h atom-value) "."]
        [:p "The current ref value is " (html/h ref-value) "."]
        (page/link-to "/" "back to guestbook")]])))

(routes/defroutes guestbook-app
  (http/POST "/sign"
    (sign-guestbook params))
  (http/GET "/"
    (show-guestbook))
  (http/GET "/exercise"
    (exercise))
  (http/ANY "*"
    [404 "Not found!"]))

(http/defservice guestbook-app)
