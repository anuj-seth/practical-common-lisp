(ns pcl.cd-db
  (:require [clojure.java.io :as io]))

(defrecord CD [title artist rating ripped?])

(def cd-db (atom []))

(defn save-db
  "Print a data structure to a file so that we may read it in later."
  [data-structure #^String filename]
  (with-open [w (io/writer filename)]
    (.write w
            (binding [*print-dup* true] (prn-str @data-structure)))))

(defn load-db [filename]
  (reset! cd-db (with-open [r (java.io.PushbackReader. (io/reader filename))]
                  (read r))))

(defn make-cd [title artist rating ripped]
  (CD. title artist rating ripped))

(defn add-record [cd]
  (swap! cd-db conj cd))

(defn dump-db []
  (doseq [cd @cd-db]
    (doseq [[k v] cd]
      (println (str (clojure.string/upper-case (name k)) ": " v)))
    (println)))

(defn prompt-read [prompt]
  (print prompt)
  (flush)
  (read-line))

(defn y-or-n-p [prompt]
  (= "y"
     (loop []
       (let [input (clojure.string/lower-case (prompt-read prompt))]
         (or (re-matches #"[yn]" input)
             (do
               (println "Please press Y or N")
               (recur)))))))

(defn prompt-for-cd []
  (make-cd (prompt-read "Title: ")
           (prompt-read "Artist: ")
           (Integer/parseInt (prompt-read "Rating: "))
           (y-or-n-p "Ripped? [y/n]: ")))

(defn add-cds []
  (loop []
    (add-record (prompt-for-cd))
    (if (y-or-n-p "Another? [y/n]")
      (recur))))

(defn where [& {:keys [artist title rating ripped] :as where-clauses}]
  (fn [record]
    (every? identity
            (for [[k v] where-clauses]
              (= (k record) v)))))

(defn select [selector-fn]
  (filter selector-fn @cd-db))

(defn update [selector-fn & {:keys [artist title rating ripped] :as update-cd}]
  (swap! cd-db (fn [current-value] 
                 (vec (map #(if (selector-fn %)
                              (merge % update-cd)
                              %) current-value)))))

(defn delete [selector-fn]
  (swap! cd-db (fn [current-value] 
                 (vec (remove selector-fn current-value)))))

;;(add-record (make-cd "Roses" "Kathy Mattea" 7 true))
;;(add-record (make-cd "Fly" "Dixie Chicks" 8 true))
;;(add-record (make-cd "Home" "Dixie Chicks" 9 true))

(dump-db)

;; (add-cds)

;;(prn @cd-db)

;;(save-db cd-db "test.db")


(count (load-db "test.db"))

(prn @cd-db)

cd-db
(prn (select (where :artist "Dixie Chicks")))

(select (where :title "Roses" :ripped? false))

;;(update (where :title "Roses" :ripped? true) :ripped? false)

(delete (where :title "Roses" :ripped? true))




