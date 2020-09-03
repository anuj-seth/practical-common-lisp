(ns pcl.chapter-9.ekal-parikshan
  (:require [clojure.pprint :as pprint]))

(def ^:dynamic *test-name* [])

(defn report-result
  [result form]
  (pprint/cl-format true "~:[FAIL~;pass~] ... ~a: ~a~%" result *test-name* form)
  result)

(defn combine-results
  [& v]
  (every? identity v))

(defmacro check
  [& forms]
  `(combine-results ~@(for [form forms]
                        `(report-result ~form '~form))))

(defmacro deftest
  [name & body]
  (println &form)
  (println &env)
  `(defn ~name
     []
     (binding [*test-name* (conj *test-name* '~name)]
       ~@body)))

(comment 
  (macroexpand-1 '(check (= (+ 1 2) 3)
                         (true? (= 1 1))))


  (macroexpand-1 '(deftest test-macro
                    (check (= (+ 1 2) 3))))

  (macroexpand '(deftest test-macro
                  (check (= (+ 1 2) 3))))

  (deftest test-macro
    (check (= (+ 1 2) 3)))

  (fn? combine-results)
  (fn? test-macro)

  (instance? clojure.lang.Symbol 'combine-results)
  (instance? clojure.lang.Symbol 'deftest)

  )
