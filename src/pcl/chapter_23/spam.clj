(ns pcl.chapter-23.spam
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(defonce max-ham-score 0.4)
(defonce min-spam-score 0.6)

(def feature-database (atom {:total-spams 0
                             :total-hams 0
                             :features {}}))

(defn update-counts-by-key
  [features-db word word-key totals-key]
  (update-in features-db
             [:features word word-key]
             inc)
  (update features-db total-key inc))

(defmulti update-counts (fn [classification _] classification))

(defmethod update-counts :spam
  [_ features-db word]
  (update-counts-by-key features-db
                        word
                        :spam-count
                        :total-spams))

(defmethod update-counts :ham
  [_ features-db word]
  (update-counts-by-key features-db
                        word
                        :ham-count
                        :total-hams))

(defmulti resource-reader (fn [file-name file-type] file-type))

(defmethod resource-reader :gzip
  [file-name _]
  (->> file-name
       io/resource
       io/input-stream
       java.util.zip.GZIPInputStream.
       io/reader))

(defmethod resource-reader :txt
  [file-name _]
  (->> file-name
       io/resource
       io/reader))

(defmethod resource-reader :default
  [file-name file-type]
  (throw (ex-info "Unsupported file type"
                  {:file-name file-name
                   :file-type file-type})))

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
                              (assoc m k
                                     {:word k :spam-count 0 :ham-count 0})))]
    (swap! feature-database
           (fn [f-db]
             (update f-db
                     :features
                     #(add-if-not-exists % word))))))

(defn extract-features
  [text]
  (doseq [word (extract-words text)]
    (intern-feature word)))

(defn increment-count
  [word spam?]
  (swap! feature-database
         update-counts
         word
         (if spam? :spam :ham)))

(defn train
  [text spam-or-ham]
  (let [spam? (= "1" spam-or-ham)]
    (doseq [word (extract-words text)]
      (intern-feature word)
      (increment-count word spam?))))

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

  (with-open [r (resource-reader "emails.csv.gz" :gzip)]
    (first (csv/read-csv r)))

  (with-open [r (resource-reader "emails.csv" :csv)]
    (frequencies 
     (mapcat (comp extract-words :text)
             (csv-data->map (csv/read-csv r)))))

  (with-open [r (io/reader (io/resource "emails.csv"))]
    (dorun
     (map (comp extract-features :text)
          (csv-data->map (csv/read-csv r)))))

  (with-open [r (resource-reader "emails.csv.gz" :gzip)]
    (doseq [line (csv-data->map (csv/read-csv r))]
      (extract-features (:text line))))

  (with-open [r (resource-reader "emails.csv.gz" :gzip)]
    (doseq [line (csv-data->map (csv/read-csv r))]
      (train (:text line) (:spam line))))

  (train "hell" "1")
  (train "world" "0")
  (reset! feature-database {})

  )
