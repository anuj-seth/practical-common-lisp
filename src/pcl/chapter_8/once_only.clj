(ns pcl.chapter-8.once-only)

(defmacro dbg-with-fn
  [f x]
  `(let [x# ~x
         print-this# (~f x#)]
     (println '(~f ~x) "=" print-this#)
     x#))
