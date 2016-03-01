(ns image-search.core
  (:require [image-lib.core :refer [find-images
                                    find-all-images
                                    image-path
                                    best-image
                                    preference]]
            [monger
             [collection :as mc]
             [core :as mg]])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
