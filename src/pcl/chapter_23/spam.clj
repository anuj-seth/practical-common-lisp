(ns pcl.chapter-23.spam
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]))


(defonce max-ham-score 0.4)
(defonce min-spam-score 0.6)

(def feature-database (atom {}))

(defrecord WordFeature [word spam-count ham-count])

(defn csv-data->map
  [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map keyword)
            repeat)
       (rest csv-data)))

(defmacro delete-duplicates
  [s]
  `(into #{} ~s))

(defn extract-words
  [text]
  (delete-duplicates
   (re-seq #"[a-zA-Z]{3,}" text)))


(defn lazy-read-csv
  [csv-file]
  (let [in-file (io/reader csv-file)
        csv-seq (csv/read-csv in-file)
        lazy (fn lazy [wrapped]
               (lazy-seq
                (if-let [s (seq wrapped)]
                  (cons (first s) (lazy (rest s)))
                  (.close in-file))))]
    (lazy csv-seq)))


(defn score
  [_]
  )

(defn intern-feature
  [word]
  (let [add-if-not-exists (fn [m k]
                            (if (contains? m k)
                              m
                              (assoc m k (->WordFeature k 0 0))))]
    (swap! feature-database
           #(add-if-not-exists % word))))

(defn extract-features
  [text]
  ;;(map intern-feature (extract-words text)
  (doseq [word (extract-words text)]
    (intern-feature word)))

(defn classification
  [score]
  (cond
    (<= score max-ham-score) :ham
    (>= score min-spam-score) :spam
    :else :unsure))

(defn classify
  [text]
  (classification (score (extract-features text))))

(comment 
  (count (keys @feature-database))
  (reset! feature-database {})
  (intern-feature "hello")
  (intern-feature "hell")

  (frequencies (extract-words "hello world hello hell"))

  ;;(take 5 (map :spam (csv-data->map (lazy-read-csv (io/resource "emails.csv")))))

  (with-open [r (io/reader (io/resource "emails.csv"))]
    (frequencies 
     (mapcat (comp extract-words :text)
             (csv-data->map (csv/read-csv r)))))

  (with-open [r (io/reader (io/resource "emails.csv"))]
    (dorun
     (map (comp extract-features :text)
          (csv-data->map (csv/read-csv r)))))

  (with-open [r (io/reader (io/resource "emails.csv"))]
    (doseq [line (csv-data->map (csv/read-csv r))]
      (extract-features (:text line))))

  (with-open [r (io/reader (io/resource "emails.csv"))]
    (let [words (csv-data->map (csv/read-csv r))]
      (first words)))

  (dorun
   (map (fn [x] (println "func called ") (map println x)) [[1 2 3] [4 5 6]]))

  (doall
   (map (fn [x] (map println x)) [[1 2 3] [4 5 6]]))

  )
