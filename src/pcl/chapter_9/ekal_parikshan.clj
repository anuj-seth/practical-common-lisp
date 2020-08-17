(ns pcl.chapter-9.ekal-parikshan
  (:require [clojure.pprint :as pprint]))

(defn report-result
  [result form]
  (pprint/cl-format true "~:[FAIL~;pass~] ... ~a~%" result form)
  result)

(defn combine-results
  [& v]
  (every? identity v))

(defmacro check
  [& forms]
  `(combine-results ~@(for [form forms]
                        `(report-result ~form '~form))))

(defn test-+
  []
  (check (= (+ 1 2) 3)
         (= (+ 1 2 3) 6)
         (= (+ -1 -3) -4)
         ;;(= (+ 1 2) 4)
         (true? (= 1 1))))

(defn test-*
  []
  (check (= (* 1 2) 2)
         (= (* 1 2 3) 6)
         (= (* -1 -3) 3)
         (= (* 1 2) 4)))

(defn test-arithmetic
  []
  (combine-results (test-+)
                   (test-*)))

(macroexpand-1 '(check (= (+ 1 2) 3)
                       (true? (= 1 1))))

;;(check (= (+ 1 2) 3))
(test-+)

(test-*)

(test-arithmetic)
