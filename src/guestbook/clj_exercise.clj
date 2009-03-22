(ns guestbook.clj-exercise)


(def a-ref (ref {:visitors [] :last-modified "Never"}))

(defn update-a-ref [key value]
  (dosync (ref-set a-ref (assoc @a-ref key value))))

(def an-atom (atom 0))

(defn increment-an-atom []
  (swap! an-atom inc))

(defn show-off [visitor]
  (dosync (update-a-ref :visitors (conj (:visitors @a-ref) visitor)))
  (update-a-ref :last-modified (java.util.Date.))
  (increment-an-atom)
  [@an-atom @a-ref])
