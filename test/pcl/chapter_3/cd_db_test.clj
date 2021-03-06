(ns pcl.chapter-3.cd-db-test
  (:require [pcl.chapter-3.cd-db :as sut]
            [clojure.test :refer :all]))

(deftest where-clause
  (is (true? ((sut/where :ripped? true) {:ripped? true})))
  (is (true? ((sut/where :rating 1 :artist "hello") {:rating 1 :artist "hello" :ripped? false})))
  (is (false? ((sut/where :artist "Shan" :title "Awara Nazme" :rating 10 :ripped? false) {:artist "Shan" :title "Awara Nazme" :rating 10 :ripped? true})))
  (is (= [{:artist "Shan" :title "Awara Nazme" :rating 10 :ripped? false}]
         (filter (sut/where :artist "Shan")
                 [{:artist "Shan" :title "Awara Nazme" :rating 10 :ripped? false}
                  {:artist "Sagrika" :title "Awara Nazme" :rating 10 :ripped? false}]))))
