(ns image-search.play
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
                                       write
                                       images
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
    first)

(class (-> all-images
     (in :Model "Nik")
     (eq :Year 2015)))

;; We can also use or
(-> all-images
    (or
     (in :Model "phone")
     (eq :ISO-Speed-Ratings 640)
     (in :Model "Nik"))
    count)

;; and and. and is not necessary normally. Using -> with a series of filters is
;; effectiely doing an and. However it can be specified so that it can be used
;; inside an or.

(-> all-images
    (or
     (in :Model "phone")
     (and (in :Model "Nik")
          (eq :Year 2015)
          (eq :Year 2016)))
    count)

;; images can be used instead of -> all-images
(images
    (and (in :Model "Nik")
         (eq :ISO-Speed-Ratings 100)
         (in :Model "phone"))
    count)

;; We can also output a list of pictures to a file. Note that the file is not
;; emptied first, up to you to rm it, if thats what you want. This example also uses
;; paths, which just outputs the path of each pic

(images
 (in :Model "phone")
 (paths)
 (write "/tmp/phone-pics"))
