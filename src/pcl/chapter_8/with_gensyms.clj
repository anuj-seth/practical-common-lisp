(ns pcl.chapter-8.with-gensyms)

;; this is a modification of the where macro from chapter 3
;; to use the with-gensyms macro rather than the explicit let

(defmacro with-gensyms
  [names body]
  `(let ~(into []
               (mapcat #(vector %1 '(gensym))
                       names))
     ~body))

;; (defmacro where
;;   [& clauses]
;;   (let [args (gensym)]
;;     `(fn [~args]
;;        (and ~@(for [[k# v#] (partition 2 clauses)]
;;                 `(= ~v# (~k# ~args)))))))



(defmacro where
  [& clauses]
  (with-gensyms [args]
    `(fn [~args]
       (and ~@(for [[k# v#] (partition 2 clauses)]
                `(= ~v# (~k# ~args)))))))

(macroexpand-1 '(where :a 1))

((where :a 1) {:a 2})

(macroexpand-1 '(with-gensyms [args]
                  `(fn [~args]
                     (and ~@(for [[k# v#] (partition 2 clauses)]
                              `(= ~v# (~k# ~args)))))))

(macroexpand-1 '(with-gensyms [x y]
                  `(fn [~x ~y]
                     (+ ~x ~y))))
(defmacro adder
  []
  (with-gensyms [x y]
    `(fn [~x ~y]
       (+ ~x ~y))))

((adder) 1 2)

(macroexpand-1 '(adder))


