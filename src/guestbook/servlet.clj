(ns guestbook.servlet
  (:gen-class
    :extends javax.servlet.http.HttpServlet)
  (:use
    compojure.http
    compojure.html)
  (:require
    [guestbook.greetings    :as greetings]
    [guestbook.clj-exercise :as clj-exercise]
    [appengine-clj.users    :as users]))


(defn show-guestbook [{:keys [user user-service]}]
  (let [all-greetings (greetings/find-all)]
    (html [:html
      [:head
        [:title "Guestbook"]
        (include-css "/stylesheets/main.css")]
      [:body
        [:h1 "AppEngine Clojure Guestbook"]
        (if user
          [:p "Hello, " (.getNickname user) "! (You can "
            (link-to (.createLogoutURL user-service "/") "sign out")
            ".)"]
          [:p "Hello! (You can "
            (link-to (.createLoginURL user-service "/") "sign in")
            " to include your name with your greeting when you post.)"])
        (if (empty? all-greetings)
          [:p "The guestbook has no messages."]
          (map (fn [greeting]
            [:div
              [:p (if (greeting :author) [:strong (greeting :author)] "An anonymous guest") " wrote:"]
              [:blockquote (h (greeting :content))]])
            all-greetings))
        (form-to [:post "/sign"]
          [:div (text-area "content" "")]
          [:div (submit-button "Post Greeting")])
        (link-to "/exercise" "exercise clojure a bit")]])))

(defn sign-guestbook [params user]
  (greetings/create (params :content) (if user (.getNickname user)))
  (redirect-to "/"))

(defn exercise [user]
  (let [[atom-value ref-value] (clj-exercise/show-off (if user (.getNickname user) "anon"))]
    (html [:html
      [:head
        [:title "Clojure on AppEngine: Atoms and Refs"]
        (include-css "/stylesheets/main.css")]
      [:body
        [:h1 "Atoms and Refs"]
        [:p "Each request to this page increments an atom, which starts at zero, 
            and updates a ref by adding to a vector of visitors and leaving a timestamp.
            This is just here to illustrate that atoms and refs work.
            But you may see with repeated requests that they work a little strangely
            due to the distributed nature of AppEngine.
            (Or you may not. It's unpredictable.)"]
        [:p "The current atom value is " (h atom-value) "."]
        [:p "The current ref value is " (h ref-value) "."]
        (link-to "/" "back to guestbook")]])))

(defroutes guestbook-app
  (POST "/sign"
    (sign-guestbook params ((users/user-info request) :user)))
  (GET "/"
    (show-guestbook (users/user-info request)))
  (GET "/exercise"
    (exercise ((users/user-info request) :user)))
  (ANY "*"
    [404 "Not found!"]))

(defservice (users/wrap-with-user-info guestbook-app))

