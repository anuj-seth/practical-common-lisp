(ns pcl.chapter-1.cd-db-test
  (:require [pcl.chapter-1.cd-db :as sut]
            [clojure.test :refer :all]))

(deftest where-clause
  (is (true? ((sut/where :a 1) {:a 1})))
  (is (true? ((sut/where :a 1 :b 2) {:a 1 :b 2 :c 3})))
  (is (false? ((sut/where :a 1 :b 2) {:a 1 :b 1 :c 3})))
  (is (false? ((sut/where :a 1) {:a 2})))
  (is (false? ((sut/where :a 1 :b 2) {:a 1}))))
