(ns pcl.chapter-3.where-macro)

;; (defmacro where
;;   [& clauses]
;;   `(fn [~'record]
;;      (println "RECORD " ~'record)
;;      (every? (fn [[~'k ~'v]] (println "KV " ~'k ~'v)
;;                (= ~'v (~'k ~'record)))
;;              (partition 2 '~clauses))))

;; (defmacro where
;;   [& clauses]
;;   `(fn [record#]
;;      (every? (fn [[k# v#]] (= v# ((keyword k#) record#)))
;;              (partition 2 '~clauses))))

(defmacro where
  [& clauses]
  `(fn [~'record]
     (and ~@(for [[k# v#] (partition 2 clauses)]
              `(= ~v# (~k# ~'record))))))

(comment 
  (let [record 1
        a 5]
    ((where :a 2) {:a 1 :b 2}))

  (let [record 1]
    (macroexpand-1 '(where :a 2 :b 1)))

  (partition 2
             (:a 2))

  )

