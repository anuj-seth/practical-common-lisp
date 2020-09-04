(ns pcl.chapter-13.destructuring-bind)

;; let's assume clojure does not have destructuring
;; and we are inventing this on our own.
;; I don't have a mental model of what the final code
;; should look like so I take one example and the code
;; i think it should generate
(defmacro destructuring-bind
  {:style/indent 2}
  [names vals & body]
  `(let [~@(interleave names vals)]
     ~@body))

(macroexpand-1 '(destructuring-bind (x y z) (1 2 3)
                  (list :x x :y y :z)))

(destructuring-bind (x y z) (1 2 3)
  (list :x x :y y :z))

;; this seems to work but we immediately run into problems when
;; the expression should be evaluated before assignment

(macroexpand-1 '(destructuring-bind (x y z) (list 1 2 3)
                  (list :x x :y y :z)))

(defmacro destructuring-bind
  {:style/indent 2}
  [names vals & body]
  `(let [expr# ~(eval vals)]
     (let [~@(interleave names expr#)]
       ~@body)))

(macroexpand-1 '(destructuring-bind (x y z) (list 1 2 3)
                  (list :x x :y y :z)))

(macroexpand-1 '(destructuring-bind (x y z) '(1 2 3)
                  (list :x x :y y :z)))

;; (destructuring-bind (x y z) (list 1 2 3)
;;   (list :x x :y y :z))
