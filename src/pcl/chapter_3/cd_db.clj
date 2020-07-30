(ns pcl.chapter-3.cd-db
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def cd-db (atom []))

(defn save-db
  "Print a data structure to a file so that we may read it in later."
  [data-structure #^String filename]
  (with-open [w (io/writer filename)]
    (.write w
            (binding [*print-dup* true]
              (prn-str data-structure)))))

(defn load-db
  [filename]
  (with-open [r (java.io.PushbackReader. (io/reader filename))]
    (reset! cd-db (read r))))

(defn make-cd
  [title artist rating ripped]
  {:title title
   :artist artist
   :rating rating
   :ripped? ripped})

(defn add-record
  [cd]
  (swap! cd-db conj cd))

(defn dump-db
  []
  (doseq [cd @cd-db]
    (doseq [[k v] cd]
      (println (str (string/upper-case (name k)) ": " v)))
    (println)))

(defn prompt-read
  [prompt]
  (print prompt)
  (flush)
  (read-line))

(defn until-matches-pattern
  [prompt pattern wrong-input-message]
  (let [input (string/lower-case (prompt-read prompt))]
    (if (re-matches pattern input)
      input
      (do
        (println wrong-input-message)
        (recur prompt
               pattern
               wrong-input-message)))))

(defn y-or-n-p
  [prompt]
  (= "y"
     (until-matches-pattern prompt
                            #"[yn]"
                            "Please press Y or N")))

(defn prompt-for-cd
  []
  (make-cd (prompt-read "Title: ")
           (prompt-read "Artist: ")
           (Integer/parseInt (prompt-read "Rating: "))
           (y-or-n-p "Ripped? [y/n]: ")))

(defn add-cds
  []
  (add-record (prompt-for-cd))
  (if (y-or-n-p "Another? [y/n]")
    (recur)))

(defn where
  [& {:keys [artist title rating ripped?] :as clauses}]
  (fn [record]
    (and (if (nil? artist) true (= (:artist record) artist))
         (if (nil? title) true (= (:title record) title))
         (if (nil? rating) true (= (:rating record) rating))
         (if (nil? ripped?) true (= (:ripped? record) ripped?)))))

(defn where
  [& {:keys [artist title rating ripped?] :as clauses}]
  (fn [record]
    (and (or (nil? artist) (= (:artist record) artist))
         (or (nil? title) (= (:title record) title))
         (or (nil? rating) (= (:rating record) rating))
         (or (nil? ripped?) (= (:ripped? record) ripped?)))))

(defn select
  [selector-fn]
  (filter selector-fn @cd-db))

(defn update
  [selector-fn & {:keys [artist title rating ripped] :as update-cd}]
  (swap! cd-db (fn [current-value] 
                 (map #(if (selector-fn %)
                         (merge % update-cd)
                         %)
                      current-value))))

(defn delete
  [selector-fn]
  (swap! cd-db (fn [current-value] 
                 (remove selector-fn current-value))))

(comment

  (add-record (make-cd "Roses" "Kathy Mattea" 7 true))
  (add-record (make-cd "Fly" "Dixie Chicks" 8 true))
  (add-record (make-cd "Home" "Dixie Chicks" 9 true))

  (dump-db)

  (add-cds)

  (prn @cd-db)

  (save-db @cd-db "test.db")


  (count (load-db "test.db"))

  (prn @cd-db)

  cd-db
  (prn (select (where :artist "Dixie Chicks")))

  (select (where :title "Roses" :ripped? true))

  (select (where :title "Fly" :ripped? true))

  (update (where :title "Roses" :ripped? true) :ripped? false)

  (delete (where :title "Roses" :ripped? false))

  (delete (where :title "Fly" :ripped? true))

  (delete (where :title "Home" :ripped? true))

  )




