(ns pcl.chapter-23.spam
  (:require [clojure.java.io :as io]))

(defonce max-ham-score 0.4)
(defonce min-spam-score 0.6)

(def feature-database (atom {}))

(defrecord WordFeature [word spam-count ham-count])


(io/resource "emails.csv")
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
