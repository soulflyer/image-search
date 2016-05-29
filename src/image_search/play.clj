(ns image-search.play
  (:require [image-lib.core :refer [find-images
                                    find-all-images
                                    image-path
                                    image-paths
                                    best-image
                                    preference
                                    find-sub-keywords]]
            [image-search.core :refer [in eq lt le gt ge
                                       or and
                                       open
                                       database
                                       image-collection
                                       keyword-collection
                                       db
                                       all-images
                                       thumbnail
                                       medium
                                       large
                                       fullsize
                                       ]]))

;; The main functions from image-lib are available here:
(find-images db image-collection "ISO-Speed-Ratings" "640")
(find-sub-keywords db keyword-collection "Iain Wood")

;; Usually we don't want all the details about the images we find
;; We can use map, and a metadata field name as a function to give us
;; just one piece of information:
(map :Project (find-images db image-collection "ISO-Speed-Ratings" "640"))

;; In the case of :Project, it is probably more useful to turn the
;; sequence into a set:
(set (map :Project (find-images db image-collection "ISO-Speed-Ratings" "640")))

;; We can also use the eq ge lt etc functions to filter a list of images
(eq (find-images db image-collection "ISO-Speed-Ratings" "640") :Project "10-Road")

;; Rather than start with a find-images function and filter it, it may be
;; easier to start with the all-images function and just use a sucession
;; of filters to get the images we want:
(count (eq
        (eq
         all-images
         :ISO-Speed-Ratings 640)
        :Exposure-Time 160))

;; The more complex the query, the harder it is to understand. Using the
;; -> syntax makes it very clear and easy to understand what is going on:
(-> all-images
    (eq :ISO-Speed-Ratings 640)
    (eq :Exposure-Time 160)
    (open medium))

;; Note that if we use in to search a metadata field that contains a
;; string, we can use an incomplete case insensitive string
(-> all-images
    (in :Model "phone")
    (eq :Year 2015)
    count)

(class (-> all-images
     (in :Model "Nik")
     (eq :Year 2015)))

;; We can also use or
(-> all-images
    (or
     (in :Model "phone")
     (eq :ISO-Speed-Ratings 640))
    count)

;; and and. and is not necessary normally. Using -> with a series of filters is
;; effectiely doing an and. However it can be specified so that it can be used
;; inside an or.

(-> all-images
    (or
     (in :Model "phone")
     (and (in :Model "Nik")
          (eq :Year 2015)))
    count)
