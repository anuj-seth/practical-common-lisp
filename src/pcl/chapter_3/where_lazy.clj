(ns pcl.chapter-3.where-lazy)

(defn where
  [& clauses]
  (fn [record]
    (every? (fn [[k v]] (= v (record k)))
            (partition 2 clauses))))
