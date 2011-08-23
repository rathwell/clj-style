(ns gaka.plus
  (:require [gaka.core :as g]))

;; defined rules by group (vector of rules for each :group)
(def defined-rules (atom {}))

(defmacro defmixin
  "Defines a new mixin with the specified name. Args should be pairs
  of properties and values."
  [name & args]
  `(def ~name (with-meta
                (-> (list ~@args) seq flatten)
                {:type :mixin})))

(defmacro defrule
  "Defines a new css rule, which is a gaka format rule. Optionally, you may
  also specify a group (as a keyword) that the rule belongs to. If no
  group is specified then :default will be used."
  [name & args]
  `(let [last# (- (count (list ~@args)) 1)
         group# (nth (list ~@args) (- last# 1) :default)
         rule# (nth (list ~@args) last#)] 
     (def ~name (with-meta rule# {:type :rule}))
     (let [rules# (->
                   (@defined-rules group#)
                   vec
                   (conj rule#))]
       (swap! defined-rules assoc group# rules#)
       nil)))

(defn render
  "Render the rule, mixin, or entire group to string. If no argument is specified,
  then the :default group will be rendered."
  ([]
     (render :default))
  ([object]
     (cond
      (keyword? object) (apply g/css (distinct (@defined-rules object)))
      :else (condp = (:type (meta object))
		:rule (g/css object)
		:mixin (g/inline-css object)))))

(defn save
  "Save the defined css rules for the specified group to the specified file. If
  the group is not specified, the :default group will be saved."
  ([filename]
     (save filename :default))
  ([filename group]
     (let [rules (distinct (@defined-rules group))]
       (apply g/save-css filename rules))))
