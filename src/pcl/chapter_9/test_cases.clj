(ns pcl.chapter-9.test-cases
  (:require  [pcl.chapter-9.ekal-parikshan :refer :all]))

(deftest test-+
  (check (= (+ 1 2) 3)
         (= (+ 1 2 3) 6)
         (= (+ -1 -3) -4)
         ;;(= (+ 1 2) 4)
         (true? (= 1 1))))

(deftest test-*
  (check (= (* 1 2) 2)
         (= (* 1 2 3) 6)
         (= (* -1 -3) 3)
         (= (* 1 2) 4)))

(deftest test-arithmetic
  (combine-results (test-+)
                   (test-*)))

(comment 
  (clojure.walk/macroexpand-all '(deftest test-math
                                   (test-arithmetic)))
  (test-+)

  (test-*)

  (test-arithmetic)

  (test-math)
  (macroexpand-1 '(deftest test-macro
                    (check (= (+ 1 2) 3))))

  )
