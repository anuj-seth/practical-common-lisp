(ns pcl.chapter-15.pathname
  (:require [clojure.java.io :as io]))

(defn path-exists?
  [path-name additional-check-fn return-value-fn]
  (let [f (io/file path-name)]
    (if (and (.exists f)
             (additional-check-fn f))
      (return-value-fn f))))

(defn file-exists?
  [file-name]
  (path-exists? file-name #(.isFile %) identity))

(defn list-directory
  [dir-name]
  (path-exists? dir-name #(.isDirectory %) file-seq))

(defn walk-directory
  "This is a side effecting function as doseq does not hold onto the head
  and returns nil"
  [dir-name apply-fn & {:keys [directories test-fn] :or {test-fn (constantly true)}}]
  (doseq [file (list-directory dir-name)]
    (cond
      (and directories
           (.isDirectory file)
           (test-fn file)) (apply-fn file)
      (.isFile file) (apply-fn file))))

(comment

  (walk-directory "/home/ubuntu/master/vodafone/server/bin" #(.getPath %) :directories true)

  (file-exists? "/home/ubuntu/prize_draw_.txt")

  (.isFile (io/file "home/ubuntu"))
  (clojure.pprint/pprint (list-directory "/home/ubuntu/master/vodafone/server/bin"))

  )
