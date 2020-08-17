(ns pcl.chapter-8.do-primes)

(defn prime?
  [n]
  (cond
    (= n 0) false
    (= n 1) false
    (= n 2) true
    :else (not-any? #(zero? (mod n %))
                    (range 2 n))))

(defmacro do-primes
  "this is like the doseq macro
   but simpler"
  {:style/indent 1}
  [[sym start end] & body]
  `(for [~sym (range ~start (inc ~end))
        :when (prime? ~sym)]
    (do ~@body)))


(macroexpand-1 '(do-primes [p 0 (rand-int 100)]
                  p))

(do-primes [x 0 (rand-int 10)] x)

(let [p -100]
  (count (do-primes [p 0 100]
           (println p)
           p)))

(do-primes p 0 100 p)
