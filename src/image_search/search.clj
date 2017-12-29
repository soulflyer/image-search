(ns image-search.search
  (:require [image-lib.images :refer [find-images]]
            [image-lib.core :refer [find-all-images best-image]]
            [image-lib.keywords :refer [find-sub-keywords]]
            [image-lib.helper :refer [image-path image-paths]]
            [image-lib.preferences :refer [preference preferences preference!]]
            [image-lib.search :refer [in eq lt le gt ge
                                      or and]]
            [image-search.core :refer [open
                                       images
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

(map image-path (images
                 (eq :ISO-Speed-Ratings 640)))

(images
 (in :Model "phone")
 (eq :Year 2015)
 (image-paths))

(images
 (in :Model "phone")
 (eq :Year 2015)
 (open medium))
