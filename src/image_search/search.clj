(ns image-search.search
  (:require [image-lib.core :refer [find-images
                                    find-all-images
                                    image-path
                                    image-paths
                                    best-image
                                    preference
                                    preferences
                                    preference!
                                    find-sub-keywords]]
            [image-search.core :refer [in eq lt le gt ge
                                       or and
                                       open
                                       paths
                                       find
                                       database
                                       image-collection
                                       keyword-collection
                                       db
                                       all-images
                                       thumbnail
                                       medium
                                       large
                                       fullsize]]))
;; Being a collection of useful searches. Start up cider and hit C-c C-c

(map image-path (find
                 (eq :ISO-Speed-Ratings 640)))

(find
 (in :Model "phone")
 (eq :Year 2015)
 (paths))

(find
 (in :Model "phone")
 (eq :Year 2015)
 (open medium))
