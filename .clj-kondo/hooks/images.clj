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
  (images  (in :Keywords "Ceratosoma trilobatum")
           (image-paths)
           (write "/tmp/ceratasoma.txt"))
  (list* (api/token-node '->)
         (list* {:a 1})
         (in :Keywords "Ceratosoma trilobatum")
         (image-paths)
         (write "/tmp/ceratasoma.txt")
         )
  (list*
    (api/token-node '->)
    [{:a 1}]
    "forms"
    )
  )
