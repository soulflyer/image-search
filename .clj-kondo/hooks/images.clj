(ns hooks.images
  (:require [clj-kondo.hooks-api :as api]))

(defn images
  [{:keys [node]}]
  (let [forms (rest (:children node))
        new-node  (api/list-node
                    (list*
                      (api/token-node '->)
                      [{:a 1}]
                      forms
                      ))]
    {:node new-node}))

(comment
  )
