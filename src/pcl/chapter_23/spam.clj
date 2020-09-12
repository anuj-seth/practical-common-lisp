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

(defn extract-words
  [text]
  
  )

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

;;(take 5 (map :spam (csv-data->map (lazy-read-csv (io/resource "emails.csv")))))

(with-open [r (io/reader (io/resource "emails.csv"))]
  (doall 
   (take 5 (csv-data->map (csv/read-csv r)))))

(defn score
  [_]
  )

(defn extract-features
  [_])

(defn classification
  [score]
  (cond
    (<= score max-ham-score) :ham
    (>= score min-spam-score) :spam
    :else :unsure))

(defn classify
  [text]
  (classification (score (extract-features text))))
